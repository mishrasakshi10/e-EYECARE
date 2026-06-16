document.addEventListener("DOMContentLoaded", () => {
    loadPatientProfile();
    document.getElementById("saveBtn").addEventListener("click", saveProfile);


document.getElementById("profilePhoto").addEventListener("change", function() {
    const file = this.files[0];
    const img = document.getElementById("profileImage");

    if (file) {
        img.src = URL.createObjectURL(file);
    }
});

});

function loadPatientProfile() {
    fetch("/e-EYECARES/PatientProfileServlet")
        .then(res => res.json())
        .then(data => {
            if (data.error) { 
                alert(data.error);
                window.location.href = "/e-EYECARES/login.html";
                return; 
            }

            document.getElementById("fullName").value = data.name;
            document.getElementById("email").value = data.email;
            document.getElementById("phone").value = data.phone || "";

            // ✅ FIX 1: correct key
            document.getElementById("profilePhoto").value = data.profilePhoto || "";

            // ✅ FIX 2: correct image path
            const img = document.getElementById("profileImg");

            if (data.profilePhoto && data.profilePhoto.trim() !== "") {
                img.src = "/e-EYECARES/images/" + data.profilePhoto;
            } else {
                img.src = "/e-EYECARES/images/user-avatar.png";
            }
        })
        .catch(err => console.error("Error loading profile:", err));
}

function saveProfile() {
    const formData = new FormData();

    formData.append("fullName", document.getElementById("fullName").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("phone", document.getElementById("phone").value);

    const fileInput = document.getElementById("profilePhoto");
    if (fileInput.files.length > 0) {
        formData.append("profilePhoto", fileInput.files[0]);
    }

    fetch("/e-EYECARES/UpdatePatientProfileServlet", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(() => {
        alert("Profile updated successfully!");
        window.location.href = "patientProfile.html";
    })
    .catch(err => console.error("Error:", err));
}