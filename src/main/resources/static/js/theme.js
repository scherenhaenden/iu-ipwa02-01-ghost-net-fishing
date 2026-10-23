function toggleTheme(event) {
    if (event && event.preventDefault) event.preventDefault();
    const body = document.body;
    body.classList.toggle('theme-dark');

    const themeName = document.getElementById('theme-name');
    const isDarkMode = body.classList.contains('theme-dark');
    if (themeName) themeName.textContent = isDarkMode ? (themeName.getAttribute('data-dark') || 'Dark') : (themeName.getAttribute('data-light') || 'Light');
    try {
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
    } catch (e) {
        // ignore storage errors (e.g., private mode)
    }
}

function toggleHighContrast() {
    const body = document.body;
    const enabled = body.classList.toggle('high-contrast');
    try {
        localStorage.setItem('highContrast', enabled);
    } catch (e) {}
}

function toggleReadableFont() {
    const body = document.body;
    const enabled = body.classList.toggle('readable-font');
    try {
        localStorage.setItem('readableFont', enabled);
    } catch (e) {}
}

function changeLanguage(lang) {
    if (!lang) return;
    try {
        // store in localStorage for client-side convenience
        localStorage.setItem('lang', lang);
    } catch (e) {}
    // navigate to same path with lang param so Spring's LocaleChangeInterceptor + CookieLocaleResolver persist it
    const url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    // Prevent duplicate param stacking
    window.location.href = url.toString();
}

function applySettings() {
    let theme = null;
    try {
        theme = localStorage.getItem('theme');
    } catch (e) {
        theme = null;
    }

    const highContrast = (function() { try { return localStorage.getItem('highContrast') === 'true'; } catch (e) { return false; } })();
    const readableFont = (function() { try { return localStorage.getItem('readableFont') === 'true'; } catch (e) { return false; } })();

    const body = document.body;
    const themeName = document.getElementById('theme-name');
    const highContrastToggle = document.getElementById('high-contrast-toggle');
    const readableFontToggle = document.getElementById('readable-font-toggle');
    const languageSelect = document.getElementById('language-select');

    if (theme === 'dark') {
        body.classList.add('theme-dark');
        if (themeName) themeName.textContent = themeName.getAttribute('data-dark') || 'Dark';
    } else {
        body.classList.remove('theme-dark');
        if (themeName) themeName.textContent = themeName.getAttribute('data-light') || 'Light';
    }

    if (highContrast) {
        body.classList.add('high-contrast');
        if (highContrastToggle) highContrastToggle.checked = true;
    } else {
        body.classList.remove('high-contrast');
        if (highContrastToggle) highContrastToggle.checked = false;
    }

    if (readableFont) {
        body.classList.add('readable-font');
        if (readableFontToggle) readableFontToggle.checked = true;
    } else {
        body.classList.remove('readable-font');
        if (readableFontToggle) readableFontToggle.checked = false;
    }

    // Sync language select with server-side locale if available, else fall back to stored value
    try {
        const stored = localStorage.getItem('lang');
        if (languageSelect) {
            // If server rendered select has a selected option (Thymeleaf), prefer it; otherwise use stored value
            if (!languageSelect.value && stored) languageSelect.value = stored;
            else if (stored && languageSelect.value !== stored) {
                // do nothing: server cookie takes precedence on reload; keep select in sync
            }
        }
    } catch (e) {}
}

// Initialize on DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', applySettings);
} else {
    applySettings();
}

// Expose functions for inline handlers (already used from templates)
window.toggleTheme = toggleTheme;
window.toggleHighContrast = toggleHighContrast;
window.toggleReadableFont = toggleReadableFont;
window.applySettings = applySettings;
window.changeLanguage = changeLanguage;
