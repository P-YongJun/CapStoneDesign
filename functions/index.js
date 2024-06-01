const functions = require('firebase-functions');
const admin = require('firebase-admin');
var serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://cap2.firebaseio.com"
    });

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendFCMNotification = functions.firestore.document('rain/{searchText}/data/{docId}')
    .onWrite((change, context) => {
        const searchText = context.params.searchText;
        const data = change.after.data();

        // FCM 메시지 생성
        const message = {
            data: {
                title: 'Firestore 데이터 변경됨',
                body: 'Firestore 데이터가 변경되었습니다.'
            },
            topic: 'allDevices'
        };

        // FCM 메시지 전송
        return admin.messaging().send(message)
            .then((response) => {
                console.log('FCM message sent successfully:', response);
                return null;
            })
            .catch((error) => {
                console.error('Error sending FCM message:', error);
                return null;
            });
    });

