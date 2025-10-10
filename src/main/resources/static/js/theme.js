/**
 * Toggles the website theme between dark and light modes.
 *
 * This function checks if the event is valid and prevents its default action.
 * It then toggles the 'theme-dark' class on the document body and updates the
 * theme name displayed in the element with the ID 'theme-name'. The current
 * theme preference is stored in localStorage, handling any potential storage
 * errors gracefully.
 *
 * @param {Event} event - The event object that triggered the theme toggle.
 */
function toggleTheme(event) {
    if (event && event.preventDefault) event.preventDefault();
    const body = document.body;
    body.classList.toggle('theme-dark');

    const themeName = document.getElementById('theme-name');
    const isDarkMode = body.classList.contains('theme-dark');
    if (themeName) themeName.textContent = isDarkMode ? 'Dark' : 'Light';
    try {
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
    } catch (e) {
        // ignore storage errors (e.g., private mode)
    }
}

/**
 * Toggles high contrast mode and saves the preference in local storage.
 */
function toggleHighContrast() {
    const body = document.body;
    const enabled = body.classList.toggle('high-contrast');
    try {
        localStorage.setItem('highContrast', enabled);
    } catch (e) {}
}

/**
 * Toggles the readable font class on the document body and stores the state in localStorage.
 */
function toggleReadableFont() {
    const body = document.body;
    const enabled = body.classList.toggle('readable-font');
    try {
        localStorage.setItem('readableFont', enabled);
    } catch (e) {}
}

/**
 * Apply user settings for theme, high contrast, and readable font.
 *
 * The function retrieves the user's preferences from localStorage for theme, high contrast, and readable font settings.
 * It then updates the document body and relevant elements based on these preferences, applying the appropriate classes
 * and toggling the state of the UI elements accordingly.
 *
 * @returns {void}
 */
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

    if (theme === 'dark') {
        body.classList.add('theme-dark');
        if (themeName) themeName.textContent = 'Dark';
    } else {
        body.classList.remove('theme-dark');
        if (themeName) themeName.textContent = 'Light';
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
