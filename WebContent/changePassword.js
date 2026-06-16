function changePassword() {

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (newPassword !== confirmPassword) {
        alert("New passwords do not match!");
        return;
    }

    const formData = new URLSearchParams();
    formData.append("currentPassword", currentPassword);
    formData.append("newPassword", newPassword);

    fetch("/e-EYECARES/ChangePasswordServlet", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            alert("Password updated successfully!");
            window.location.href = "patient/patientProfile.html";
        } else {
            alert(data.error);
        }
    })
    .catch(err => console.error("Error:", err));
}