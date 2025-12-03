document.addEventListener('DOMContentLoaded', () => {

    // --- Lógica de Estilos de Formulario ---

    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.querySelectorAll('.form-control').forEach(input => {

            // Efecto al ganar foco
            input.addEventListener('focus', function () {
                const icon = this.parentNode.querySelector('lord-icon');
                if (icon) {
                    // Cambia el color del ícono al enfocar
                    // Debes adaptar esta línea para que coincida con la librería lord-icon si es necesario
                    icon.style.opacity = 1;
                }
                this.classList.add('focused');
            });

            // Efecto al perder foco
            input.addEventListener('blur', function () {
                if (!this.value) {
                    const icon = this.parentNode.querySelector('lord-icon');
                    if (icon) {
                        // Vuelve el ícono a su estado normal si está vacío
                        icon.style.opacity = 0.7;
                    }
                }
                this.classList.remove('focused');
            });

            // Validación al escribir (principalmente para el email)
            input.addEventListener('input', function () {
                this.classList.remove('success', 'error');

                if (this.type === 'email' && this.value.length > 0) {
                    // Expresión regular básica para validar el formato de email
                    const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.value);
                    if (isValid) {
                        this.classList.add('success');
                    } else {
                        this.classList.add('error');
                    }
                }

                // Limpiar validación si se borra el campo
                if (this.value.length === 0) {
                    this.classList.remove('success', 'error');
                }
            });
        });
    }
});