// =======================
// Patient Profile JS
// =======================

// Run when page loads
window.onload = function() {
    loadProfile();
    checkTheme();
};

// Fetch patient profile from servlet
function loadProfile() {
    fetch("/e-EYECARES/PatientProfileServlet")
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            alert("Please login as a patient first!");
            window.location.href = "/e-EYECARES/login.html";
            return;
        }

        // Update profile info
        document.getElementById("nameHeader").innerText = data.name;
        document.getElementById("name").innerText = data.name;
        document.getElementById("email").innerText = data.email;
        document.getElementById("phone").innerText = data.phone || "Not Provided";

       const profileImg = document.getElementById("profileImage");

if (data.profilePhoto && data.profilePhoto.trim() !== "") {
    profileImg.src = "/e-EYECARES/images/" + data.profilePhoto;
} else {
    profileImg.src = "/e-EYECARES/images/user-avatar.png";
}

    })
    .catch(error => console.error("Error fetching profile:", error));
}

// Edit profile button
function editProfile() {
    window.location.href = "/e-EYECARES/patient/editPatientProfile.html";
}

// Change password button
function changePassword() {
    window.location.href = "/e-EYECARES/changePassword.html";
}

// Logout function
function logout() {
    fetch("/e-EYECARES/LogoutServlet")
    .then(() => window.location.href = "/e-EYECARES/login.html")
    .catch(err => console.error("Logout error:", err));
}

// =======================
// Dark Mode Toggle
// =======================
function toggleTheme() {
    document.body.classList.toggle("dark-mode");
    localStorage.setItem("darkMode", document.body.classList.contains("dark-mode"));
}

// Check dark mode preference on page load
function checkTheme() {
    const darkMode = localStorage.getItem("darkMode") === "true";
    if (darkMode) document.body.classList.add("dark-mode"); 
}
