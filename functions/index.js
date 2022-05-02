const functions = require('firebase-functions');

let admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref("/offline_messages/{offline_message_id}")
.onWrite((change, context) => {
    let offline_message_receiver_id = change.after.child("receiver_id").val();
    console.log("offline_message_receiver_id:", offline_message_receiver_id);
    
    let reference = admin.database().ref("/users/"+offline_message_receiver_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "offline_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});


exports.sendGroupChatNotification = functions.database.ref("/group_offline_messages/{group_offline_message_id}")
.onWrite((change, context) => {
    let group_offline_message_receiver_ids = change.after.child("receiver_ids").val();
    console.log("group_offline_message_receiver_ids:", group_offline_message_receiver_ids);
    
    let tokens = [];
    
    for(var i = 0; i < group_offline_message_receiver_ids.length; i++){
        let reference = admin.database().ref("/users/"+group_offline_message_receiver_ids[i]);
        reference.once("value").then(snap => {
            let token = snap.child("messaging_token").val();
            tokens.push(token);
            console.log("token:", token);
            if(i === group_offline_message_receiver_ids.length){
                var options = {
                  priority: "high",
                  timeToLive: 60 * 60 * 24
                };
                const payload = {
                    data: {
                        data_type: "group_offline_message"
                    }
                };
                return admin.messaging().sendToDevice(tokens, payload, options)
                .then(function(response) {
                    console.log("Successfully sent messages:", response);
                  })
                .catch(function(error) {
                    console.log("Error sending messages:", error);
                });   
            }
        });
    }
});


exports.sendGroupInvitationNotification = functions.database.ref("/group_chat_invitations/{group_chat_invitation_id}")
.onWrite((change, context) => {
    let group_chat_invitation_receiver_ids =change.after.child("receiver_ids").val();
    console.log("group_chat_invitation_receiver_ids:", group_chat_invitation_receiver_ids);

    let tokens = [];
    
    for(var i = 0; i < group_chat_invitation_receiver_ids.length; i++){
        let reference = admin.database().ref("/users/"+group_chat_invitation_receiver_ids[i]);
        reference.once("value").then(snap => {
            let token = snap.child("messaging_token").val();
            tokens.push(token);
            console.log("token:", token);
            if(i === group_chat_invitation_receiver_ids.length){
                var options = {
                  priority: "high",
                  timeToLive: 60 * 60 * 24
                };
                const payload = {
                    data: {
                        data_type: "group_invitation_message"
                    }
                };
                return admin.messaging().sendToDevice(tokens, payload, options)
                .then(function(response) {
                    console.log("Successfully sent message:", response);
                  })
                .catch(function(error) {
                    console.log("Error sending message:", error);
                }); 
            }

        });
    }
});



exports.sendPostUploadedNotification = functions.database.ref("/creator_posts/{creator_post_id}")
.onWrite((change, context) => {
    let creator_post_receiver_ids = change.after.child("receiver_ids").val();
    console.log("creator_post_receiver_ids:", creator_post_receiver_ids);

    let tokens = [];
    
    for(var i = 0; i < creator_post_receiver_ids.length; i++){
        let reference = admin.database().ref("/users/"+creator_post_receiver_ids[i]);
        reference.once("value").then(snap => {
            let token = snap.child("messaging_token").val();
            tokens.push(token);
            console.log("token:", token);
            if(i === creator_post_receiver_ids.length){
                var options = {
                  priority: "high",
                  timeToLive: 60 * 60 * 24
                };
                const payload = {
                    data: {
                        data_type: "creator_post_message"
                    }
                };
                return admin.messaging().sendToDevice(tokens, payload, options)
                .then(function(response) {
                    console.log("Successfully sent message:", response);
                  })
                .catch(function(error) {
                    console.log("Error sending message:", error);
                }); 
            }

        });
    }
});


exports.sendCreatorFollowNotification = functions.database.ref("/creator_follows/{creator_follow_id}")
.onWrite((change, context) => {
    let creator_follows_messages_creator_id = change.after.child("creator_id").val();
    console.log("creator_follows_messages_creator_id:", creator_follows_messages_creator_id);
    
    let reference = admin.database().ref("/users/"+creator_follows_messages_creator_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "creator_follow_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});



exports.sendCreatorUnFollowNotification = functions.database.ref("/creator_unfollows/{creator_unfollow_id}")
.onWrite((change, context) => {
    let creator_unfollows_messages_creator_id = change.after.child("creator_id").val();
    console.log("creator_unfollows_messages_creator_id:", creator_unfollows_messages_creator_id);
    
    let reference = admin.database().ref("/users/"+creator_unfollows_messages_creator_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "creator_unfollow_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});



exports.sendCreatorPostCommentNotification = functions.database.ref("/creator_post_comments/{creator_post_comment_id}")
.onWrite((change, context) => {
    let creator_post_comment_messages_creator_id = change.after.child("creator_id").val();
    console.log("creator_post_comment_messages_creator_id:", creator_post_comment_messages_creator_id);
    
    let reference = admin.database().ref("/users/"+creator_post_comment_messages_creator_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "creator_post_comment_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});



exports.sendCreatorPostLikeNotification = functions.database.ref("/creator_post_likes/{creator_post_like_id}")
.onWrite((change, context) => {
    let creator_post_like_messages_creator_id = change.after.child("creator_id").val();
    console.log("creator_post_like_messages_creator_id:", creator_post_like_messages_creator_id);
    
    let reference = admin.database().ref("/users/"+creator_post_like_messages_creator_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "creator_post_like_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});



exports.sendCreatorPostUnLikeNotification = functions.database.ref("/creator_post_unlikes/{creator_post_unlike_id}")
.onWrite((change, context) => {
    let creator_post_unlike_messages_creator_id = change.after.child("creator_id").val();
    console.log("creator_post_unlike_messages_creator_id:", creator_post_unlike_messages_creator_id);
    
    let reference = admin.database().ref("/users/"+creator_post_unlike_messages_creator_id);
    reference.once("value").then(snap => {
        let token = snap.child("messaging_token").val();
        console.log("token:", token);
        var options = {
          priority: "high",
          timeToLive: 60 * 60 * 24
        };
        const payload = {
            data: {
                data_type: "creator_post_unlike_message"
            }
        };
        return admin.messaging().sendToDevice(token, payload, options)
        .then(function(response) {
            console.log("Successfully sent message:", response);
          })
        .catch(function(error) {
            console.log("Error sending message:", error);
          });
    });
});
