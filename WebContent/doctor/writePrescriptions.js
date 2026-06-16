document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("prescriptionForm");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const appointmentId = document.getElementById("appointment_id").value.trim();
        const diagnosis = document.getElementById("diagnosis").value.trim();
        const medicines = document.getElementById("medicines").value.trim();
        const notes = document.getElementById("notes").value.trim();

        if (!appointmentId || !diagnosis || !medicines) {
            alert("Please fill all required fields marked with *");
            return;
        }

        try {
            const formData = new URLSearchParams();
            formData.append("appointment_id", appointmentId);
            formData.append("diagnosis", diagnosis);
            formData.append("medicines", medicines);
            formData.append("notes", notes);

            const response = await fetch("/e-EYECARES/WritePrescriptionsServlet", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: formData.toString(),
                credentials: "same-origin"
            });

            const result = await response.json();
            alert(result.message);

            if (result.status === "success") form.reset();

        } catch (error) {
            alert("Error submitting prescription: " + error);
            console.error(error);
        }
    });
});

function goToDashboard() {
    window.location.href = "dashboard.html"; // change if your dashboard page name is different
}