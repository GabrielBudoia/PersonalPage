// Mobile menu: shows/hides the nav when the hamburger button is clicked.
const menuBtn = document.querySelector('.menu-btn');
const nav = document.getElementById('nav');

menuBtn.addEventListener('click', () => {
    const isOpen = nav.classList.toggle('open');
    menuBtn.setAttribute('aria-expanded', isOpen);
    menuBtn.setAttribute('aria-label', isOpen ? 'Close menu' : 'Open menu');
});

// Close the menu after clicking a link inside it.
nav.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
        nav.classList.remove('open');
        menuBtn.setAttribute('aria-expanded', false);
        menuBtn.setAttribute('aria-label', 'Open menu');
    });
});