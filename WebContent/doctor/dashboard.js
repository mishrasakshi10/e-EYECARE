// Redirect to pages
function openPage(page) {
    window.location.href = window.location.origin + "/e-EYECARES/" + page;
}
// Logout function
function logout() {
    window.location.href = window.location.origin + "/e-EYECARES/LogoutServlet";
}



//Fetch doctor name and validate session
window.onload = function() {
    fetch(window.location.origin + "/e-EYECARES/DoctorDashboardServlet")
    .then(response => {
        if(!response.ok) throw new Error("Unauthorized");
        return response.json();
    })
    .then(data => {
        document.getElementById("welcomeDoctor").innerText = "Welcome, Dr. " + data.name;
    })
    .catch(error => {
        // If session expired or unauthorized, redirect to login
        window.location.href = window.location.origin + "/e-EYECARES/login.html";
    });
};
