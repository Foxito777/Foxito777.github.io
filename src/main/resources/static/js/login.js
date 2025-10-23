// corregido por jesus laura ramos
// Variable global para guardar usuario
    let userData = null;

    // Mostrar/ocultar contraseña
    const togglePass = document.getElementById("togglePass");
    const passInput = document.getElementById("password");
    togglePass.addEventListener("click", () => {
      const type = passInput.getAttribute("type") === "password" ? "text" : "password";
      passInput.setAttribute("type", type);
      togglePass.innerHTML = type === "password" ? '<i class="bi bi-eye"></i>' : '<i class="bi bi-eye-slash"></i>';
    });

    // Modal registro
    const btnShowRegister = document.getElementById("btnShowRegister");
    const registerModal = new bootstrap.Modal(document.getElementById("registerModal"));

    btnShowRegister.addEventListener("click", () => {
      registerModal.show();
    });

    // Registrar usuario
    document.getElementById("registerForm").addEventListener("submit", function(e){
      e.preventDefault();
      const email = document.getElementById("regEmail").value;
      const password = document.getElementById("regPassword").value;

      if(email && password.length >= 4){
        userData = { email, password };
        alert("Usuario registrado con éxito. Ahora puedes iniciar sesión.");

        // Limpiar inputs del formulario de registro
        this.reset();
        registerModal.hide();
      }
    });

    // Login
    document.getElementById("loginForm").addEventListener("submit", function(e){
      e.preventDefault();
      const email = document.getElementById("email").value;
      const password = document.getElementById("password").value;
      const alertError = document.getElementById("alert-error");

      if(userData && email === userData.email && password === userData.password){
        alertError.classList.add("d-none");
        alert("Bienvenido " + email);
        window.location.href = "index.html";
      } else {
        alertError.classList.remove("d-none");
      }
    });