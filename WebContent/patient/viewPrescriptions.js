document.addEventListener("DOMContentLoaded", function () {

    fetch("/e-EYECARES/ViewPrescriptionsServlet")
        .then(response => response.json())
        .then(data => {

            const container = document.getElementById("prescriptionContainer");

            if (!data || data.length === 0) {
                container.innerHTML = "<p class='no-data'>No prescriptions found</p>";
                return;
            }

            data.forEach(p => {

                const formattedDate = new Date(p.date).toLocaleString();

                let card = `
                <div class="prescription-card">

                    <div class="hospital-header">
                        <h1>e-EYECARE</h1>
                        <p>e-Eye Care & Vision Hospital</p>
                        <p>24/7 Care | Contact: +91 9876543210</p>
                    </div>

                    <hr>

                    <div class="prescription-info">
                        <p><strong>Prescription ID:</strong> ${p.prescription_id}</p>
                        <p><strong>Date:</strong> ${formattedDate}</p>
                        <p><strong>Doctor:</strong> Dr. ${p.doctor}</p>
                    </div>

                    <div class="section">
                        <h3>Diagnosis</h3>
                        <p>${p.diagnosis}</p>
                    </div>

                    <div class="section">
                        <h3>Medicines</h3>
                        <p>${p.medicines}</p>
                    </div>

                    <div class="section">
                        <h3>Advice</h3>
                        <p>${p.notes}</p>
                    </div>

                    <div class="signature">
                        <p>Doctor Signature</p>
                        <div class="line"></div>
                    </div>

                </div>
                `;

                container.innerHTML += card;
            });

        })
        .catch(error => {
            console.error("Error:", error);
        });
});

function printPage() {
    window.print();
}
