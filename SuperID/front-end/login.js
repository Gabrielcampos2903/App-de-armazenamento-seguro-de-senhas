
  // Importa os módulos corretamente
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-app.js";
import { getAuth, signInWithEmailAndPassword } from "https://www.gstatic.com/firebasejs/9.6.1/firebase-auth.js";
import { getAnalytics } from "firebase/analytics";

// Sua configuração Firebase
const firebaseConfig = {
  apiKey: "AIzaSyD84xiH2qKZ11AWIGKi4XfQbjAWDWvN1dY",
  authDomain: "superid-turma2-4.firebaseapp.com",
  projectId: "superid-turma2-4",
  storageBucket: "superid-turma2-4.firebasestorage.app",
  messagingSenderId: "650939520911",
  appId: "1:650939520911:web:4f077a3fe04cd5ca285dfa",
  measurementId: "G-X88H3364FM"
};

// Inicialize o Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);

// Evento de submit
const submit = document.getElementById('submit');
submit.addEventListener("click", function (event) {
  event.preventDefault();

  // Inputs
  const email = document.getElementById('email').value;
  const password = document.getElementById('password').value;

  signInWithEmailAndPassword(auth, email, password)
    .then((userCredential) => {
      // Usuário autenticado
      const user = userCredential.user;
      alert("Login realizado com sucesso!");
      window.location.href = "bemvindo.html"
    })
    .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;
      alert(errorMessage);
    });
});