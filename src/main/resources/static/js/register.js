// register.js

// Mostrar / ocultar contraseña
function togglePassword(btnId, inputId) {
  const btn = document.getElementById(btnId);
  const input = document.getElementById(inputId);
  if (!btn || !input) return;

  btn.addEventListener('click', () => {
    const isPass = input.type === 'password';
    input.type = isPass ? 'text' : 'password';
    const icon = btn.querySelector('i');
    if (icon) icon.className = isPass ? 'bi bi-eye-slash' : 'bi bi-eye';
  });
}

// Validación Bootstrap + coincidencia de contraseñas + antirebote
(() => {
  'use strict';

  const forms = document.querySelectorAll('.needs-validation');
  Array.from(forms).forEach(form => {
    const pass1 = form.querySelector('#password');
    const pass2 = form.querySelector('#confirmPassword');
    const submitBtn = form.querySelector('button[type="submit"]');

    // Validación en tiempo real para contraseñas
    const validatePasswords = () => {
      if (pass1 && pass2) {
        if (pass1.value !== pass2.value) {
          pass2.setCustomValidity('Mismatch');
        } else {
          pass2.setCustomValidity('');
        }
      }
    };

    if (pass1) pass1.addEventListener('input', validatePasswords);
    if (pass2) pass2.addEventListener('input', validatePasswords);

    form.addEventListener('submit', (event) => {
      validatePasswords();

      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      } else {
        // Evita doble envío
        if (submitBtn) {
          submitBtn.disabled = true;
          submitBtn.innerHTML = 'Registrando...';
        }
      }

      form.classList.add('was-validated');
    }, false);
  });
})();

// Inicializa toggles de contraseña
togglePassword('togglePass', 'password');
togglePassword('togglePass2', 'confirmPassword');
