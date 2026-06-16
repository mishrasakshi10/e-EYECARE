document.addEventListener("DOMContentLoaded", () => {
    loadDoctorProfile();
    loadThemePreference();
});

// Load Doctor Profile Data
function loadDoctorProfile() {
    fetch("/e-EYECARES/DoctorProfileServlet")
        .then(response => {
            if (!response.ok) throw new Error("Servlet not reachable");
            return response.json();
        })
        .then(data => {
            console.log("Profile Data:", data);

            // Fill basic fields
            document.getElementById("name").textContent = data.name || "N/A";
            document.getElementById("email").textContent = data.email || "N/A";
            document.getElementById("phone").textContent = data.phone || "N/A";
            document.getElementById("nameHeader").textContent = data.name || "Doctor";

            // Fill doctor_details fields (handle nulls)
            document.getElementById("specialization").textContent = data.specialization || "Not set";
            document.getElementById("qualification").textContent = data.qualification || "Not set";
            document.getElementById("experience").textContent = data.experience != null ? data.experience + " years" : "Not set";
            document.getElementById("registrationNo").textContent = data.registrationNo || "Not set";
            document.getElementById("consultationFee").textContent = data.consultationFee != null ? "$" + data.consultationFee : "Not set";

            // Profile photo
           const profileImg = document.getElementById("profileImage");

let imgName = data.profilePhoto;

// Clean value properly
if (!imgName || imgName === "null" || imgName.trim() === "") {
    imgName = "doctor-image.jpg";
} else {
    imgName = imgName.trim();
}

// FINAL PATH
profileImg.src = "/e-EYECARES/images/" + imgName;

// Debug
console.log("Final Image Path:", profileImg.src);
        })
        .catch(err => {
            console.error("Error loading profile:", err);
            document.getElementById("name").textContent = "Error loading profile";
        });
}

// Edit Profile (for future editable page)
function editProfile() {
    window.location.href = "/e-EYECARES/doctor/editDoctorProfile.html";
}

// Change Password
function changePassword() {
    window.location.href = "/e-EYECARES/changePassword.html";
}

// Logout
function logout() {
    if (confirm("Are you sure you want to logout?")) {
        window.location.href = "/e-EYECARES/LogoutServlet";
    }
}

// Dark Mode Toggle
function toggleTheme() {
    document.body.classList.toggle("dark-mode");
    const isDark = document.body.classList.contains("dark-mode");
    localStorage.setItem("doctorTheme", isDark ? "dark" : "light");
}

// Load saved theme preference
function loadThemePreference() {
    const savedTheme = localStorage.getItem("doctorTheme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark-mode");
        const themeSwitch = document.getElementById("themeSwitch");
        if (themeSwitch) themeSwitch.checked = true;
    }
}