//   partículas flotantes
function createParticle() {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 8 + 3;
    particle.style.width = size + 'px';
    particle.style.height = size + 'px';
    particle.style.left = Math.random() * 100 + '%';
    particle.style.animationDuration = (Math.random() * 10 + 8) + 's';
    particle.style.animationDelay = Math.random() * 5 + 's';
    document.body.appendChild(particle);

    setTimeout(() => {
        particle.remove();
    }, 18000);
}

//    partículas constantemente
setInterval(createParticle, 400);

//     partículas al cargar
for (let i = 0; i < 10; i++) {
    createParticle();
}

//   los iconos flotantes
document.addEventListener('mousemove', (e) => {
    const icons = document.querySelectorAll('.floating-icon');
    const mouseX = e.clientX / window.innerWidth;
    const mouseY = e.clientY / window.innerHeight;

    icons.forEach((icon, index) => {
        const speed = (index + 1) * 10;
        const x = (mouseX - 0.5) * speed;
        const y = (mouseY - 0.5) * speed;
        icon.style.transform = `translate(${x}px, ${y}px)`;
    });
});

//   brillo al mover el mouse sobre el contenedor
const container = document.querySelector('.error-container');
container.addEventListener('mousemove', (e) => {
    const rect = container.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    container.style.background = `
            radial-gradient(circle at ${x}px ${y}px,
                rgba(255, 140, 0, 0.15),
                rgba(26, 15, 8, 0.8) 50%)
        `;
});

container.addEventListener('mouseleave', () => {
    container.style.background = 'rgba(26, 15, 8, 0.8)';
});