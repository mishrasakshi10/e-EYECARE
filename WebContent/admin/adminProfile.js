document.addEventListener("DOMContentLoaded", () => {
    loadAdminProfile();
    loadThemePreference();
});

function loadAdminProfile() {
    fetch("/e-EYECARES/AdminProfileServlet")
        .then(res => res.json())
        .then(data => {

            document.getElementById("name").textContent = data.name || "Admin";
            document.getElementById("email").textContent = data.email || "admineyecare1031@gmail.com";
            document.getElementById("phone").textContent = data.phone || "9518771287";
            document.getElementById("nameHeader").textContent = data.name || "Admin";

        })
        .catch(err => {
            console.error("Error:", err);
            document.getElementById("name").textContent = "Error loading profile";
        });
}

function changePassword() {
    window.location.href = "/e-EYECARES/changePassword.html";
}

function logout() {
    if (confirm("Are you sure you want to logout?")) {
        window.location.href = "/e-EYECARES/LogoutServlet";
    }
}

function toggleTheme() {
    document.body.classList.toggle("dark-mode");
    const isDark = document.body.classList.contains("dark-mode");
    localStorage.setItem("adminTheme", isDark ? "dark" : "light");
}

function loadThemePreference() {
    const savedTheme = localStorage.getItem("adminTheme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark-mode");
        document.getElementById("themeSwitch").checked = true;
    }
}