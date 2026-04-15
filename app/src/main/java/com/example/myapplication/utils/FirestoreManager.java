package com.example.myapplication.utils;

import com.example.myapplication.activities.establishment.models.Establishment;
import com.example.myapplication.activities.establishment.repositories.EstablishmentRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    private static FirestoreManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public String getUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public Task<DocumentSnapshot> getUserDetails() {
        String uid = getUserId();
        if (uid == null) return Tasks.forException(new Exception("Not logged in"));
        return db.collection("users").document(uid).get();
    }

    public Task<AuthResult> createUser(String name, String email, String password) {
        return auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("email", email);
                db.collection("users").document(user.getUid()).set(userData);
            }
        });
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    private CollectionReference getEstablishmentsRef() {
        String userId = getUserId();
        if (userId == null) return null;
        return db.collection("users").document(userId).collection("establishments");
    }

    public Task<Void> loadUserData() {
        String userId = getUserId();
        if (userId == null) return Tasks.forException(new Exception("User not authenticated"));

        CollectionReference establishmentsRef = getEstablishmentsRef();
        if (establishmentsRef == null) return Tasks.forResult(null);
        Task<QuerySnapshot> establishmentsTask = establishmentsRef.get();

        return establishmentsTask.continueWith(task -> {
            if (task.isSuccessful()) {
                List<Establishment> establishments = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Establishment e = doc.toObject(Establishment.class);
                    if (e != null) {
                        e.setId(doc.getId());
                        establishments.add(e);
                    }
                }
                EstablishmentRepository.getInstance().setEstablishmentList(establishments);
            }
            return null;
        });
    }

    public void addOrUpdateEstablishment(Establishment establishment) {
        CollectionReference ref = getEstablishmentsRef();
        if (ref == null) return;

        if (establishment.getId() == null) {
            List<Establishment> currentList = EstablishmentRepository.getInstance().getEstablishmentList().getValue();
            if (currentList != null) {
                for (Establishment e : currentList) {
                    if (e.getName().equalsIgnoreCase(establishment.getName())) {
                        establishment.setId(e.getId());
                        break;
                    }
                }
            }
        }

        if (establishment.getId() == null) {
            ref.add(establishment).addOnSuccessListener(docRef -> loadUserData());
        } else {
            ref.document(establishment.getId()).set(establishment).addOnSuccessListener(aVoid -> loadUserData());
        }
    }

    public void deleteEstablishment(Establishment establishment) {
        CollectionReference ref = getEstablishmentsRef();
        if (ref == null || establishment.getId() == null) return;
        ref.document(establishment.getId()).delete().addOnSuccessListener(aVoid -> loadUserData());
    }
}
