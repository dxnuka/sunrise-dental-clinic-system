</main>
<footer class="footer">&copy; 2026 Sunrise Dental Clinic &mdash; Internal staff system.</footer>
<script>
(function() {
    var key = 'scrollPos:' + location.pathname;
    window.addEventListener('beforeunload', function() {
        try { sessionStorage.setItem(key, String(window.scrollY)); } catch (e) {}
    });
    window.addEventListener('DOMContentLoaded', function() {
        try {
            var saved = sessionStorage.getItem(key);
            if (saved !== null) {
                requestAnimationFrame(function() { window.scrollTo(0, parseInt(saved, 10)); });
            }
        } catch (e) {}
    });
})();

var EYE_OPEN_SVG = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7-11-7-11-7Z"/><circle cx="12" cy="12" r="3"/></svg>';
var EYE_CLOSED_SVG = '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19c-7 0-11-7-11-7a18.6 18.6 0 0 1 5.06-5.94M9.9 4.24A10.4 10.4 0 0 1 12 4c7 0 11 7 11 7a18.6 18.6 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><path d="M1 1l22 22"/></svg>';

function initPasswordToggle(btn) {
    btn.innerHTML = EYE_OPEN_SVG;
}
document.querySelectorAll('.password-toggle').forEach(initPasswordToggle);

document.addEventListener('click', function(e) {
    var btn = e.target.closest('.password-toggle');
    if (!btn) return;
    var input = document.getElementById(btn.getAttribute('data-target'));
    if (!input) return;
    if (input.type === 'password') {
        input.type = 'text';
        btn.innerHTML = EYE_CLOSED_SVG;
        btn.setAttribute('aria-label', 'Hide password');
    } else {
        input.type = 'password';
        btn.innerHTML = EYE_OPEN_SVG;
        btn.setAttribute('aria-label', 'Show password');
    }
});
</script>
</body>
</html>
