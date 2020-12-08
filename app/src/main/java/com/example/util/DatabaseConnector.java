package com.example.util;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.util.Interfaces.MyStatListener;
import com.example.util.Interfaces.SubjectList;
import com.example.util.Models.PhysicianChoiceModel;
import com.example.util.Models.PhysicianDetailModel;
import com.example.util.Models.SubjectDetailModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
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


  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private CollectionReference collectionReference;

  private final CollectionReference dataCollectionReference = db.collection("Data");
  private final CollectionReference mapCollectionReference = db.collection("Map");

  private final static String TAG = "Database Connector";
  private final static String PhysicianChoiceDocName = "PhysicianSetup";

  /**
   * Constructor, Does not take any parameters
   */
  public DatabaseConnector() {

    try {
      firebaseAuth = FirebaseAuth.getInstance();
      //Firebase Instance..
      authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
          FirebaseUser user = firebaseAuth.getCurrentUser();
          if (user != null) {
            Log.d(TAG, "onAuthStateChanged: ***********" + user.getUid());
          } else Log.d(TAG, "onAuthStateChanged: ______________");
        }
      };
      firebaseAuth.addAuthStateListener(authStateListener);
    } catch (Exception e) {
      Log.d(TAG, "DatabaseConnector: Constructor | Error: " + e.getMessage());
    }

  }

  /**
   * Destructor
   */
  protected void finalize() {
    if (currentUser != null && firebaseAuth != null) {
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


  public String getCurrentUserId() {
    return FirebaseAuth.getInstance().getCurrentUser().getUid();
  }

  /**
   * This Method can be used to check if the user is already logged in.
   *
   * @param myStatListener The Interface for callbacks, MyStatListener.
   */
  public void checkAlreadyLogin(MyStatListener myStatListener) {
    currentUser = firebaseAuth.getCurrentUser();
    if (currentUser != null) myStatListener.status(true, null);
    else myStatListener.status(false, null);
  }

  /**
   * This method can be used in validating the login credentials of the Physician.
   *
   * @param email    Email of the user created in the Firebase eg. ram@hi.com
   * @param password Password linked with the email.
   * @param myStatListener  for callbacks, MyStatListener
   */
  public void validateLogin(final String email, String password, final MyStatListener myStatListener) {

    firebaseAuth = FirebaseAuth.getInstance();
    if (!EntityClass.getInstance().isSubject())
      PhysicianDetailModel.getInstance().setPhysicianEmail(email);
    else SubjectDetailModel.getInstance().setSubjectEmail(email);

    firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              Log.d(TAG, "validateLogin: **********" + firebaseAuth.getCurrentUser().getUid());
              currentUser = FirebaseAuth.getInstance().getCurrentUser();
              final String currentUserId = currentUser.getUid();
              Log.d(TAG, "onComplete: " + currentUserId);
              if (!EntityClass.getInstance().isSubject()) {
                dataCollectionReference.document(email).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                          if (EntityClass.getInstance().isSubject()) {
                            SubjectDetailModel.getInstance().setSubjectName(documentSnapshot.getString("UserName"));
                            SubjectDetailModel.getInstance().setUserIdInDb(currentUserId);
                          } else {
                            PhysicianDetailModel.getInstance().setPhysicianName(documentSnapshot.getString("UserName"));
                            PhysicianDetailModel.getInstance().setUserIdInDb(currentUserId);
                          }
                          myStatListener.status(true, null);
                        } else {
                          Log.d(TAG, "onSuccess: validateLogin| Record not exist.");
                          myStatListener.onFailure("Record not exists");
                          myStatListener.status(false, null);
                        }
                      }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: validateLogin| " + e.getMessage());
                        myStatListener.onFailure(e.getMessage());
                        myStatListener.status(false, null);
                      }
                    });
              } else {
                mapCollectionReference.document(email).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                          if (!documentSnapshot.getString("physicianEmail").isEmpty()) {
                            PhysicianDetailModel.getInstance().setPhysicianEmail(documentSnapshot.getString("physicianEmail"));

                            SubjectDetailModel.getInstance().setUserIdInDb(documentSnapshot.getString("UserIdInDB"));
                            dataCollectionReference.document(PhysicianDetailModel.getInstance().getPhysicianEmail()).collection(email).document("SubjectData")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                  @Override
                                  public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    SubjectDetailModel.getInstance().setSubjectName(documentSnapshot.getString("UserName"));
                                    myStatListener.status(true, null);
                                  }
                                }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onSuccess: validateLogin| Record data not exist.");
                                myStatListener.onFailure("Record data not exists");
                                myStatListener.status(false, null);
                              }
                            });
                          } else {
                            // patient without physician...
                          }
                        } else {
                          Log.d(TAG, "onSuccess: validateLogin| Record Map not exist.");
                          myStatListener.onFailure("Record Map not exists");
                          myStatListener.status(false, null);
                        }
                      }
                    }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: validateLogin| " + e.getMessage());
                    myStatListener.onFailure(e.getMessage());
                    myStatListener.status(false, null);
                  }
                });
              }
              //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            } else {
              Log.d(TAG, "onComplete: " + task.getException().getMessage());
            }

          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure: validateLogin|  " + e.getMessage());
        myStatListener.onFailure(e.getMessage());
        myStatListener.status(false, null);
      }
    });

  }

  /**
   * This method can be used to create a new Physician account.
   *
   * @param loginEmail  Email of the user
   * @param loginPassword Password of the User
   * @param loginUserName userID
   * @param myStatListener       Interface for callbacks, MyStatListener
   */
  public void createUserAccount(final String loginEmail, final String loginPassword, final String loginUserName, final MyStatListener myStatListener) {

    firebaseAuth = FirebaseAuth.getInstance();
    firebaseAuth.createUserWithEmailAndPassword(loginEmail, loginPassword)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              currentUser = FirebaseAuth.getInstance().getCurrentUser();
              assert currentUser != null;
              final String currentUserId = currentUser.getUid();
              final Map<String, String> userMap = new HashMap<>();
              userMap.put("UserIdInDB", currentUserId);
              userMap.put("UserName", loginUserName);

              if (EntityClass.getInstance().isSubject()) {
                userMap.put("Age", SubjectDetailModel.getInstance().getSubjectAge());
                if (PhysicianDetailModel.getInstance().getPhysicianEmail() != null && PhysicianDetailModel.getInstance().getPhysicianEmail() != "") {
                  dataCollectionReference.document(PhysicianDetailModel.getInstance().getPhysicianEmail())
                      .get()
                      .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                          if (task.getResult().exists()) {
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
                                            Map<String, String> newMap = new HashMap<>();
                                            newMap.put("UserIdInDB", currentUserId);
                                            newMap.put("physicianEmail", PhysicianDetailModel.getInstance().getPhysicianEmail());
                                            mapCollectionReference.document(loginEmail).set(newMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid) {
                                                    SubjectDetailModel.getInstance().setSubjectName(loginUserName);
                                                    SubjectDetailModel.getInstance().setSubjectEmail(loginEmail);
                                                    SubjectDetailModel.getInstance().setUserIdInDb(currentUserId);
                                                    myStatListener.status(true, null);
                                                  }
                                                }).addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.getMessage());
                                                myStatListener.status(false, null);
                                                myStatListener.onFailure(e.getMessage());
                                              }
                                            });
                                          }
                                        }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        myStatListener.status(false, null);
                                        myStatListener.onFailure(e.getMessage());
                                      }
                                    });
                                  }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                    myStatListener.status(false, null);
                                    myStatListener.onFailure(e.getMessage());
                                  }
                                });
                          } else {
                            firebaseAuth.getCurrentUser().delete();
                            Log.d(TAG, "onComplete: Unable to find the Physician Email");
                            myStatListener.status(false, null);
                            myStatListener.onFailure("Unable to find the Physician Email");
                          }
                        }
                      }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.d(TAG, "onFailure: " + e.getMessage());
                      myStatListener.status(false, null);
                      myStatListener.onFailure(e.getMessage());
                    }
                  });

                } else {
                  // code for patients without physicians.
                }
              } else {
                // for physicians
                dataCollectionReference.document(loginEmail).set(userMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                        PhysicianDetailModel.getInstance().setPhysicianName(loginUserName);
                        PhysicianDetailModel.getInstance().setPhysicianEmail(loginEmail);
                        PhysicianDetailModel.getInstance().setUserIdInDb(currentUserId);
                        myStatListener.status(true, null);
                      }
                    }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    myStatListener.status(false, null);
                    myStatListener.onFailure(e.getMessage());
                  }
                });
              }
            }
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure: " + e.getMessage());
        myStatListener.status(false, null);
        myStatListener.onFailure(e.getMessage());
      }
    });

  }

  /**
   * This method can be used to get the list of all the patients under a physician.
   *
   * @param listner Interface for callbacks, SubjectList interface.
   */
  public void getSubjectsList(final SubjectList listner) {

    String path = "Data/" + PhysicianDetailModel.getInstance().getPhysicianEmail();
    db.document(path)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful() && task.getResult().exists()) {
              List<String> subjectData = new ArrayList<String>();

              for (Map.Entry<String, Object> subjectsList : task.getResult().getData().entrySet()) {
                if (subjectsList.getKey() == "UserName" || subjectsList.getKey() == "UserIdInDB")
                  continue;
                subjectData.add(subjectsList.getValue() + "\n" + "email:" + subjectsList.getKey());
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
   *
   * @param email   Email of the patient
   * @param name    Name of the patient
   * @param myStatListener Interface for callbacks, MyStatListener
   */
  public void checkExistingSubject(final String email, final String name, final MyStatListener myStatListener) {

    dataCollectionReference.document(PhysicianDetailModel.getInstance().getPhysicianEmail()).get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot documentSnapshot) {
            documentSnapshot.getReference().collection(email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (!task.getResult().isEmpty()) {
                      SubjectDetailModel.getInstance().setSubjectName(name);
                      SubjectDetailModel.getInstance().setSubjectEmail(email);
                      for(DocumentSnapshot snapshot: task.getResult().getDocuments()) {
                        if(snapshot.getId().equals("SubjectData")) {
                          SubjectDetailModel.getInstance().setUserIdInDb(snapshot.get("UserIdInDB").toString());
                          break;
                        }
                      }
                      if(TextUtils.isEmpty(SubjectDetailModel.getInstance().getUserIdInDb())) {
                        Log.d(TAG, "onFailure: Unable to find UserIdInBb");
                        myStatListener.status(false, null);
                        myStatListener.onFailure("Unable to find UserIdInBb");
                      } else myStatListener.status(true, null);
                    } else {
                      Log.d(TAG, "onFailure: Unable to Find the User Record");
                      myStatListener.status(false, null);
                      myStatListener.onFailure("Unable to Find the User Record");
                    }
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    myStatListener.status(false, null);
                    myStatListener.onFailure(e.getMessage());
                  }
                });
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d(TAG, "onFailure: " + e.getMessage());
            myStatListener.status(false, null);
            myStatListener.onFailure(e.getMessage());
          }
        });
  }

  /**
   * This Method can be used upload image to the database.
   *
   * @param imageUri The image uri that needs to be uploaded.
   * @param myStatListener  Interface for callbacks, MyStatListener
   */
  public void saveSubjectImage(Uri imageUri, final MyStatListener myStatListener) {
    storageReference = FirebaseStorage.getInstance().getReference();
    collectionReference = db.collection("Users");
    final StorageReference filePath = storageReference.child("Subjects_Images").child(SubjectDetailModel.getInstance().getSubjectEmail());
    filePath.putFile(imageUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
              @Override
              public void onSuccess(Uri uri) {
                final String uriLink = uri.toString();
                collectionReference.whereEqualTo("UserIdInDB", SubjectDetailModel.getInstance().getUserIdInDb()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {

                          snapshots.getReference().collection(SubjectDetailModel.getInstance().getSubjectEmail()).document("SubjectData").update("imageUri", uriLink);
                          myStatListener.status(true, null);
                        }
                      }
                    }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                    myStatListener.status(false, null);
                    myStatListener.onFailure(e.getMessage());
                  }
                });


              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                myStatListener.status(false, null);
                myStatListener.onFailure(e.getMessage());
              }
            });

          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        Log.d(TAG, "onFailure: " + e.getMessage());
        myStatListener.status(false, null);
        myStatListener.onFailure(e.getMessage());
      }
    });
  }

  /**
   * This method can be used to retrieve image from the database.
   *
   * @param myStatListener For callback, MyStatListener
   */
  public void getSubjectImage(final MyStatListener myStatListener) throws StorageException {
    try {
      storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://response-counter-138b6.appspot.com");
      StorageReference filePath = storageReference.child("Subjects_Images").child(SubjectDetailModel.getInstance().getSubjectEmail()).child("imageUri.jpg");

       filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
          if (uri != null) {
            myStatListener.status(true, uri);
          } else {
            Log.d(TAG, "onSuccess: " + "Uri Error");
            myStatListener.status(false, null);
            myStatListener.onFailure("Uri Error");
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.d(TAG, "onFailure: " + e.getMessage());
          myStatListener.status(false, null);
          myStatListener.onFailure(e.getMessage());
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  /**
   * This method can be used to save object data in the database given the path.
   *
   * @param path    The file path where data must be stored in the database
   * @param data    The object containing the data that needs to be stored.
   * @param myStatListener The Interface for the callbacks, MyStatListener.
   */
  public void saveData(String path, final Object data, final MyStatListener myStatListener) {
    dataCollectionReference.document(PhysicianDetailModel.getInstance().getPhysicianEmail() + '/' + SubjectDetailModel.getInstance().getSubjectEmail() + '/' + path)
        .set(data)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            myStatListener.status(true, null);
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        myStatListener.onFailure(e.getMessage());
      }
    });
  }

  /**
   * This method is used to send the link to registered email to reset the password.
   *
   * @param email   The registered email of the user.
   * @param myStatListener The interface for the compatibility with callbacks.
   */
  public void forgotPassword(String email, final MyStatListener myStatListener) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              myStatListener.status(true, "Email sent.");
            } else if (task.getException() != null)
              myStatListener.status(false, task.getException().getMessage().toString());
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        myStatListener.status(false, e.getMessage().toString());
      }
    });
  }

  /**
   * This method is used to update the physician settings as of which activities must be enabled for patient.
   *
   * @param myStatListener - an Interface of type MyStatListener.
   */
  public void updatePhysicianControl(final MyStatListener myStatListener) {
    if (!EntityClass.getInstance().getPhysicianChoiceList().isEmpty()) {
      DatabaseReference realDb = FirebaseDatabase.getInstance().getReference("Patient Access");

      Map<String, Object> settingMap = new HashMap<>();
      for (PhysicianChoiceModel setting : EntityClass.getInstance().getPhysicianChoiceList()) {
        settingMap.put(setting.getLable(), String.valueOf(setting.isValue()));
      }
      realDb.child(SubjectDetailModel.getInstance().getUserIdInDb()).updateChildren(settingMap)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              myStatListener.status(true, null);
            }
          }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.d(TAG, "onFailure: " + e.getMessage());
          myStatListener.status(false, null);
          myStatListener.onFailure(e.getMessage());
        }
      });
    } else {
      myStatListener.status(false, null);
      myStatListener.onFailure("Empty setup list");
    }
  }

  /**
   * This method can be used to retrieve the physician settings from the database.
   * THE PHYSICIAN EMAIL MUST BE ALREADY RETRIEVED FROM THE DATABASE OR PROVIDED FROM UI
   *
   * @param myStatListener - an interface for the callbacks, MyStatListener
   */
  public void getPhysicianControl(final MyStatListener myStatListener) {
    if (PhysicianDetailModel.getInstance().getPhysicianEmail().isEmpty()) return;
    DatabaseReference realDb = FirebaseDatabase.getInstance().getReference("Patient Access").child(SubjectDetailModel.getInstance().getUserIdInDb());

    realDb.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        List<PhysicianChoiceModel> physicianChoiceList = new ArrayList<>();
        PhysicianChoiceModel model;
        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() >0) {
          for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
            model = new PhysicianChoiceModel();
            model.setLable(snapshot.getKey());
            model.setValue(Boolean.valueOf(snapshot.getValue().toString()));
            physicianChoiceList.add(model);
          }
        } else {
          for(SetupOptions option: SetupOptions.values()) {
            model = new PhysicianChoiceModel();
            model.setLable(EntityClass.getInstance().getLbl(option));
            model.setValue(false);
            physicianChoiceList.add(model);
          }
        }
        EntityClass.getInstance().setPhysicianChoiceList(physicianChoiceList);
        myStatListener.status(true, null);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG, "onCancelled: " + databaseError.toException().toString());
        myStatListener.status(false, null);
        myStatListener.onFailure(databaseError.toException().toString());
      }
    });
  }
}
