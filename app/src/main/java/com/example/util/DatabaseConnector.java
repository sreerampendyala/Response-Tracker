package com.example.util;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.util.Interfaces.DataInterfaces.DataSaveInterface;
import com.example.util.Interfaces.DataInterfaces.SubjectList;
import com.example.util.Interfaces.ValidationInterfaces.CheckLoggedInInterface;
import com.example.util.Interfaces.ValidationInterfaces.CredValidationInterface;
import com.example.util.Interfaces.DataInterfaces.ImageInterface;
import com.example.util.Interfaces.ValidationInterfaces.SignUpInterface;
import com.example.util.Interfaces.ValidationInterfaces.SubjectInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnector {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference storageReference;
    private final static String TAG = "Database Connector";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    /**
     *  Constructor, Does not take any parameters
     */
    public DatabaseConnector() {

        try {
            firebaseAuth = FirebaseAuth.getInstance();
            //Firebase Instance..
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user !=null) {
                        Log.d(TAG, "onAuthStateChanged: ***********" + user.getUid());
                    } else Log.d(TAG, "onAuthStateChanged: ______________");
                }
            };
            firebaseAuth.addAuthStateListener(authStateListener);
        } catch (Exception e) {
            Log.d("DatabaseConnector", "DatabaseConnector: Constructor | Error: " + e.getMessage());
        }

    }

    /**
     * Destructor
     */
    protected void finalize() {
        if(currentUser != null && firebaseAuth != null) {
            Log.d(TAG, "finalize: @@@@@@@@@@@@");
            firebaseAuth.signOut();
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    /**
     * This method can be used to signOut of Firebase.
     */
    public void firebaseSignOut() {
        firebaseAuth.signOut();
    }

    /**
     * This Method can be used to check if the user is already logged in.
     * @param listner The Interface for callbacks, CheckLoggedInInterface.
     */
    public void checkAlreadyLogin(CheckLoggedInInterface listner) {
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) listner.isLoggedIn(true);
        else listner.isLoggedIn(false);
    }

    /**
     * This method can be used in validating the login credentials of the Physician.
     * @param email Email of the user created in the Firebase eg. ram@hi.com
     * @param password Password linked with the email.
     * @param listner for callbacks, CredValidationInterface
     */
    public void validateLogin(final String email, String password, final CredValidationInterface listner) {

        collectionReference = db.collection("Data");
        firebaseAuth = FirebaseAuth.getInstance();
        if(!EntityClass.getInstance().isSubject()) EntityClass.getInstance().setPhysicianEmail(email);
        else EntityClass.getInstance().setSubjectEmail(email);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "validateLogin: **********" +firebaseAuth.getCurrentUser().getUid());
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            final String currentUserId = currentUser.getUid();
                            Log.d(TAG, "onComplete: " + currentUserId);
                            collectionReference.document(email).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()) {
                                                EntityClass entityObj = EntityClass.getInstance();
                                                entityObj.setUserName(documentSnapshot.getString("UserName"));
                                                entityObj.setUserIdInDb(currentUserId);
                                                listner.onSuccessValidatingCredentials(true);
                                            } else {
                                                Log.d("Database Connector", "onSuccess: validateLogin| Record not exist.");
                                                listner.onFailure("Record not exists");
                                                listner.onSuccessValidatingCredentials(false);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("Database Connector", "onFailure: validateLogin| " + e.getMessage());
                                            listner.onFailure(e.getMessage());
                                            listner.onSuccessValidatingCredentials(false);
                                        }
                                    });

                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Database Connector", "onFailure: validateLogin|  " + e.getMessage());
                        listner.onFailure(e.getMessage());
                        listner.onSuccessValidatingCredentials(false);
                    }
                });

    }

    /**
     * This method can be used to create a new Physician account.
     * @param loginEmail Email of the user
     * @param loginPassword Password of the User
     * @param loginUserName  userID
     * @param listner Interface for callbacks, SignupInterface
     */
    public void createUserAccount(final String loginEmail, final String loginPassword, final String loginUserName, final SignUpInterface listner) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert currentUser!= null;
                            final String currentUserId = currentUser.getUid();
                            final Map<String, String> userMap = new HashMap<>();
                            userMap.put("UserIdInDB", currentUserId);
                            userMap.put("UserName", loginUserName);

                            final String collectionName = "Data";
                            if (EntityClass.getInstance().isSubject()) {
                                if (EntityClass.getInstance().getPhysicianEmail() != null && EntityClass.getInstance().getPhysicianEmail() != "") {
                                    db.document("Data/" + EntityClass.getInstance().getPhysicianEmail())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.getResult().exists()) {
                                                        final DocumentSnapshot documentSnapshot = task.getResult();
                                                        task.getResult().getReference().update(loginEmail, loginUserName)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        documentSnapshot.getReference()
                                                                                .collection(loginEmail)
                                                                                .document("SubjectData")
                                                                                .set(userMap)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        String collectionName = "Map";

                                                                                        Map<String, String> newMap = new HashMap<>();
                                                                                        newMap.put("UserIdInDB", currentUserId);
                                                                                        newMap.put("physicianEmail", EntityClass.getInstance().getPhysicianEmail());
                                                                                        collectionReference = db.collection(collectionName);
                                                                                        collectionReference.document(loginEmail).set(newMap)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        EntityClass entityObj = EntityClass.getInstance();
                                                                                                        entityObj.setUserName(loginUserName);
                                                                                                        entityObj.setUserIdInDb(currentUserId);
                                                                                                        listner.signUpStatus(true);
                                                                                                    }
                                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Log.d(TAG, "onFailure: " + e.getMessage());
                                                                                                listner.signUpStatus(false);
                                                                                                listner.onFailure(e.getMessage());
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d(TAG, "onFailure: " + e.getMessage());
                                                                                listner.signUpStatus(false);
                                                                                listner.onFailure(e.getMessage());
                                                                            }
                                                                        });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                                                        listner.signUpStatus(false);
                                                                        listner.onFailure(e.getMessage());
                                                                    }
                                                                });
                                                    } else {
                                                        firebaseAuth.getCurrentUser().delete();
                                                        Log.d(TAG, "onComplete: Unable to find the Physician Email");
                                                        listner.signUpStatus(false);
                                                        listner.onFailure("Unable to find the Physician Email");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                            listner.signUpStatus(false);
                                            listner.onFailure(e.getMessage());
                                        }
                                    });
                                    
                                } else {
                                    // code for patients without physicians.
                                }
                            } else {
                                // for physicians
                                collectionReference = db.collection(collectionName);
                                collectionReference.document(loginEmail).set(userMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                EntityClass entityObj = EntityClass.getInstance();
                                                entityObj.setUserName(loginUserName);
                                                entityObj.setUserIdInDb(currentUserId);
                                                listner.signUpStatus(true);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        listner.signUpStatus(false);
                                        listner.onFailure(e.getMessage());
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                listner.signUpStatus(false);
                listner.onFailure(e.getMessage());
            }
        });

    }

    /**
     * This method can be used to get the list of all the patients under a physician.
     * @param listner Interface for callbacks, SubjectList interface.
     */
    public void getSubjectsList(final SubjectList listner){

        String path = "Data/" + EntityClass.getInstance().getPhysicianEmail();
        db.document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult().exists()) {
                            List<String> subjectData = new ArrayList<String>();

                            for (Map.Entry<String, Object> subjectsList: task.getResult().getData().entrySet()) {
                                if(subjectsList.getKey() == "UserName" || subjectsList.getKey() == "UserIdInDB") continue;
                                subjectData.add(subjectsList.getValue() + "\n" + subjectsList.getKey());
                            }
                            listner.getSubjectListStatus(true, subjectData);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        List<String> subjectData = new ArrayList<String>();
                        Log.d(TAG, "onFailure: GetSubjectList" + e.getMessage());
                        listner.getSubjectListStatus(false, subjectData);
                        listner.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * This method can be used to check existing patient
     * @param email Email of the patient
     * @param name  Name of the patient
     * @param listner Interface for callbacks, SubjectInterface
     */
    public void checkExistingSubject(final String email, final String name, final SubjectInterface listner) {
        collectionReference = db.collection("Users");

        collectionReference.whereEqualTo("UserIdInDB", EntityClass.getInstance().getUserIdInDb()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot snapshots: queryDocumentSnapshots) {

                            snapshots.getReference().collection(email).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(!task.getResult().isEmpty()) {
                                                EntityClass.getInstance().setSubjectName(name);
                                                EntityClass.getInstance().setSubjectEmail(email);
                                                listner.subjectExistOrCreated(true);
                                            } else {
                                                Log.d(TAG, "onFailure: Unable to Find the User Record");
                                                listner.subjectExistOrCreated(false);
                                                listner.onFailure("Unable to Find the User Record");
                                            }
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        listner.subjectExistOrCreated(false);
                        listner.onFailure(e.getMessage());
                    }
                });

    }

    /**
     * This method can be used to create a new patient.
     * @param email Email of the patient
     * @param name Name of the patient
     * @param listner Interface for the callbacks, SubjectInterface
     */
    public void createNewSubject(final String email, final String name, final SubjectInterface listner) {
        collectionReference = db.collection("Users");

        collectionReference.whereEqualTo("UserIdInDB", EntityClass.getInstance().getUserIdInDb()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshots: queryDocumentSnapshots) {

                            snapshots.getReference().collection(email).document("SubjectData").get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                listner.subjectExistOrCreated(false);
                                                listner.onFailure("A subject with name: " + documentSnapshot.get("SubjectName") + " already exists with the id " + documentSnapshot.get("SubjectId"));
                                            } else {
                                                Map<String, String> subjectMap = new HashMap<>();
                                                subjectMap.put("SubjectId", email);
                                                subjectMap.put("SubjectName", name);
                                                documentSnapshot.getReference().set(subjectMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                EntityClass.getInstance().setSubjectName(name);
                                                                EntityClass.getInstance().setSubjectEmail(email);
                                                                listner.subjectExistOrCreated(true);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "onFailure: " + e.getMessage());
                                                                listner.subjectExistOrCreated(false);
                                                                listner.onFailure(e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.getMessage());
                                            listner.subjectExistOrCreated(false);
                                            listner.onFailure(e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        listner.subjectExistOrCreated(false);
                        listner.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * This Method can be used upload image to the database.
     * @param imageUri The image uri that needs to be uploaded.
     * @param listner Interface for callbacks, ImageInterface
     */
    public void saveSubjectImage(Uri imageUri, final ImageInterface listner) {
        storageReference = FirebaseStorage.getInstance().getReference();
        collectionReference = db.collection("Users");
        final StorageReference filePath = storageReference.child("Subjects_Images").child(EntityClass.getInstance().getSubjectEmail());
        filePath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String uriLink = uri.toString();
                                collectionReference.whereEqualTo("UserIdInDB", EntityClass.getInstance().getUserIdInDb()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (QueryDocumentSnapshot snapshots: queryDocumentSnapshots) {

                                                    snapshots.getReference().collection(EntityClass.getInstance().getSubjectEmail()).document("SubjectData").update("imageUri",uriLink);
                                                    listner.statusAndUri(true, null);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.getMessage());
                                                listner.statusAndUri(false, null);
                                                listner.onFailure(e.getMessage());
                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        listner.statusAndUri(false, null);
                                        listner.onFailure(e.getMessage());
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        listner.statusAndUri(false, null);
                        listner.onFailure(e.getMessage());
                    }
                });
    }

    /**
     * This method can be used to retrieve image from the database.
     * @param listner For callback, Imageinterface
     */
    public void getSubjectImage(final ImageInterface listner) {
        try {
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference filePath = storageReference.child("Subjects_Images").child(EntityClass.getInstance() .getSubjectEmail());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri != null) {
                        listner.statusAndUri(true, uri);
                    } else {
                        Log.d(TAG, "onSuccess: " + "Uri Error");
                        listner.statusAndUri(false, null);
                        listner.onFailure("Uri Error");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    listner.statusAndUri(false, null);
                    listner.onFailure(e.getMessage());
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "getSubjectImage: " + ex.getClass().toString() + ": " + ex.getMessage());
        }

    }

    /**
     * This method can be used to save object data in the database given the path.
     * @param path The file path where data must be stored in the database
     * @param data The object containing the data that needs to be stored.
     * @param listner The Interface for the callbacks, DataSaveInterface.
     */
    public void saveData(String path, final Object data, final DataSaveInterface listner) {
        collectionReference = db.collection("Users");
        final String savePath = path + Timestamp.now().toString();
        collectionReference.whereEqualTo("UserIdInDB", EntityClass.getInstance().getUserIdInDb()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                            snapshot.getReference().collection(EntityClass.getInstance().getSubjectEmail()).document(savePath).set(data);
                        }
                        listner.successStatus(true);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Database Connector| saveTapData| onFailure: " + e.getMessage());
                        listner.successStatus(false);
                        listner.onFailure(e.getMessage());
                    }
                });

    }

}
