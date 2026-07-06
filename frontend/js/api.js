// ============================================
// PlacementPrep — API helper
// Centralizes all backend calls & JWT handling
// ============================================

const API_BASE_URL = "http://localhost:8080/api";

const Auth = {
    getToken() {
        return localStorage.getItem("pp_token");
    },
    setSession(authResponse) {
        localStorage.setItem("pp_token", authResponse.token);
        localStorage.setItem("pp_user", JSON.stringify({
            id: authResponse.userId,
            name: authResponse.name,
            email: authResponse.email,
            role: authResponse.role
        }));
    },
    getUser() {
        const raw = localStorage.getItem("pp_user");
        return raw ? JSON.parse(raw) : null;
    },
    isLoggedIn() {
        return !!this.getToken();
    },
    logout() {
        localStorage.removeItem("pp_token");
        localStorage.removeItem("pp_user");
        window.location.href = "login.html";
    },
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = "login.html";
        }
    }
};

async function apiRequest(path, { method = "GET", body = null, auth = true } = {}) {
    const headers = { "Content-Type": "application/json" };

    if (auth && Auth.getToken()) {
        headers["Authorization"] = `Bearer ${Auth.getToken()}`;
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : null
    });

    if (response.status === 401) {
        // Token expired or invalid — send back to login
        Auth.logout();
        return;
    }

    const contentType = response.headers.get("content-type") || "";
    const data = contentType.includes("application/json") ? await response.json() : null;

    if (!response.ok) {
        const message = (data && data.message) || "Something went wrong. Please try again.";
        throw new Error(message);
    }

    return data;
}

// Simple toast notification helper used across pages
function showToast(message, type = "info") {
    const toast = document.createElement("div");
    toast.className = "pp-toast";
    if (type === "error") toast.style.background = "#dc2626";
    if (type === "success") toast.style.background = "#16a34a";
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Dark mode toggle, persisted in localStorage
function initTheme() {
    const saved = localStorage.getItem("pp_theme") || "light";
    document.documentElement.setAttribute("data-theme", saved);
}

function toggleTheme() {
    const current = document.documentElement.getAttribute("data-theme") || "light";
    const next = current === "light" ? "dark" : "light";
    document.documentElement.setAttribute("data-theme", next);
    localStorage.setItem("pp_theme", next);
}

initTheme();
