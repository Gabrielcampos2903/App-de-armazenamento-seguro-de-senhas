const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.checkEmailVerified = functions.https.onRequest(async (req, res) => {
  // Permitir apenas POST
  if (req.method !== "POST") {
    return res.status(405).send({error: "Only POST requests are allowed"});
  }

  const email = req.body.email;

  if (!email) {
    return res.status(400).send({error: "Email is required"});
  }

  try {
    const userRecord = await admin.auth().getUserByEmail(email);
    return res.status(200).send({verified: userRecord.emailVerified});
  } catch (error) {
    return res.status(404).send({error: "User not found"});
  }
});
