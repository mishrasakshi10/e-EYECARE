document.addEventListener("DOMContentLoaded", () => {
    loadDoctorProfile();
    document.getElementById("saveBtn").addEventListener("click", saveProfile);

    document.getElementById("profilePhoto").addEventListener("change", function() {
    const file = this.files[0];
    const img = document.getElementById("profileImage");

    if (file) {
        img.src = URL.createObjectURL(file);
    }
});



});

function loadDoctorProfile() {
    fetch("/e-EYECARES/DoctorProfileServlet")
        .then(res => res.json())
        .then(data => {
            if (data.error) { alert(data.error); return; }

            document.getElementById("fullName").value = data.name;
            document.getElementById("email").value = data.email;
            document.getElementById("phone").value = data.phone;
            document.getElementById("qualification").value = data.qualification;
            document.getElementById("specialization").value = data.specialization;
            document.getElementById("experience").value = data.experience;
            document.getElementById("registrationNo").value = data.registrationNo;
            document.getElementById("consultationFee").value = data.consultationFee;
            document.getElementById("profilePhoto").value = data.profilePhoto || "";
           const img = document.getElementById("profileImage");

let imgName = data.profilePhoto;

if (!imgName || imgName.trim() === "") {
    imgName = "doctor-image.jpg";
}

img.src = "/e-EYECARES/images/" + imgName;
        })
        .catch(err => console.error("Error loading profile:", err));
}

function saveProfile() {
    const formData = new FormData();

    formData.append("fullName", document.getElementById("fullName").value);
    formData.append("email", document.getElementById("email").value);
    formData.append("phone", document.getElementById("phone").value);
    formData.append("qualification", document.getElementById("qualification").value);
    formData.append("specialization", document.getElementById("specialization").value);
    formData.append("experience", document.getElementById("experience").value);
    formData.append("registrationNo", document.getElementById("registrationNo").value);
    formData.append("consultationFee", document.getElementById("consultationFee").value);

    const fileInput = document.getElementById("profilePhoto");
    if (fileInput.files.length > 0) {
        formData.append("profilePhoto", fileInput.files[0]);
    }

    fetch("/e-EYECARES/UpdateDoctorProfileServlet", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(() => {
        alert("Profile updated successfully!");
        window.location.href = "doctorProfile.html";
    })
    .catch(err => console.error("Error:", err));
}