// Redirect to pages
function openPage(page) {
    window.location.href = window.location.origin + "/e-EYECARES/" + page;
}
// Logout function
function logout() {
    window.location.href = window.location.origin + "/e-EYECARES/LogoutServlet";
}

// Fetch patient name and validate session
window.onload = function() {
    fetch(window.location.origin + "/e-EYECARES/PatientDashboardServlet")
    .then(response => {
        if(!response.ok) throw new Error("Unauthorized");
        return response.json();
    })
    .then(data => {
        document.getElementById("welcomeUser").innerText = "Welcome, " + data.name;
    })
    .catch(error => {
        // If session expired or unauthorized, redirect to login
        window.location.href = window.location.origin + "/e-EYECARES/login.html";
    });
};
