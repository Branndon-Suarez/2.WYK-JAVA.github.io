// EN static/js/toads-sweetalert2.js (CORREGIDO)

// Las variables 'successMessage' y 'errorMessage' deben estar definidas
// en el ámbito global del HTML antes de que este script se ejecute.

// Ejecutamos la lógica inmediatamente después de la definición de las variables en el HTML.

if (typeof Swal !== 'undefined') {
    if (successMessage) {
        Swal.fire({
            toast: true,
            icon: "success",
            title: successMessage,
            position: "top-end",
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.onmouseenter = Swal.stopTimer;
                toast.onmouseleave = Swal.resumeTimer;
            }
        });
    }

    if (errorMessage) {
        Swal.fire({
            toast: true,
            icon: "error",
            title: errorMessage,
            position: "top-end",
            showConfirmButton: false,
            timer: 2000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.onmouseenter = Swal.stopTimer;
                toast.onmouseleave = Swal.resumeTimer;
            }
        });
    }
}