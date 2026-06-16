// Navigate to pages
function openPage(page) {
    window.location.href = window.location.origin + "/e-EYECARES/" + page;
}

// Logout
function logout() {
    window.location.href = window.location.origin + "/e-EYECARES/LogoutServlet";
}

// Load dashboard data
function loadDashboard() {

    fetch(window.location.origin + "/e-EYECARES/AdminDashboardServlet")
        .then(res => {
            if (res.status === 401) {
                throw new Error("Unauthorized");
            }
            return res.json();
        })
        .then(data => {

            // Welcome message
            document.getElementById("welcomeAdmin").innerText =
                "Welcome, " + data.name;

            // Dashboard statistics
            document.getElementById("pendingDoctors").innerText =
                data.pendingDoctors;

            document.getElementById("todaysAppointments").innerText =
                data.todaysAppointments;

            document.getElementById("totalPatients").innerText =
                data.totalPatients;

            document.getElementById("totalPrescriptions").innerText =
                data.totalPrescriptions;
        })
        .catch(error => {
            console.error("Dashboard Error:", error);

            // Redirect if session expired
            window.location.href =
                window.location.origin + "/e-EYECARES/login.html";
        });
}

// Run when page loads
document.addEventListener("DOMContentLoaded", function () {
    loadDashboard();

    // 🔁 Optional: Auto refresh every 30 seconds
    setInterval(loadDashboard, 30000);
});