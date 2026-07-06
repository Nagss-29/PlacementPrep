// ============================================
// PlacementPrep — Auth page logic (login & register)
// ============================================

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value;
            const btn = document.getElementById("loginBtn");

            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Logging in...';

            try {
                const res = await apiRequest("/auth/login", {
                    method: "POST",
                    body: { email, password },
                    auth: false
                });
                Auth.setSession(res);
                showToast("Welcome back!", "success");
                setTimeout(() => window.location.href = "dashboard.html", 500);
            } catch (err) {
                showToast(err.message, "error");
                btn.disabled = false;
                btn.textContent = "Login";
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const name = document.getElementById("name").value.trim();
            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            const btn = document.getElementById("registerBtn");

            if (password !== confirmPassword) {
                showToast("Passwords do not match", "error");
                return;
            }

            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Creating account...';

            try {
                const res = await apiRequest("/auth/register", {
                    method: "POST",
                    body: { name, email, password, confirmPassword },
                    auth: false
                });
                Auth.setSession(res);
                showToast("Account created! Redirecting...", "success");
                setTimeout(() => window.location.href = "dashboard.html", 500);
            } catch (err) {
                showToast(err.message, "error");
                btn.disabled = false;
                btn.textContent = "Create Account";
            }
        });
    }
});
