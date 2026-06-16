document.addEventListener("DOMContentLoaded", function () {

    console.log("JS Loaded");

    const doctorSelect = document.getElementById("doctorSelect");

     console.log("Doctor Select Element:", doctorSelect); // ADD THIS

    const slotSelect = document.getElementById("slotSelect");

    // Load Doctors
    fetch('/e-EYECARES/LoadDoctorsServlet')
        .then(response => response.json())
        .then(data => {

            doctorSelect.innerHTML = '<option value="">-- Select Doctor --</option>';

            data.forEach(doc => {

                console.log("Doctor Object:", doc);   // ADD THIS


                let option = document.createElement("option");

              option.value = doc.doctor_id;
                option.textContent = doc.name;
                doctorSelect.appendChild(option);
            });
        });

    // Attach change event properly
    doctorSelect.addEventListener("change", function () {

        let doctorId = doctorSelect.value;
        console.log("Doctor Selected:", doctorId);

        slotSelect.innerHTML = '<option value="">-- Select Slot --</option>';

        if (!doctorId) return;

        fetch('/e-EYECARES/LoadSlotsServlet?doctorId=' + doctorId)
            .then(response => response.json())
            .then(data => {

                console.log("Slots Received:", data);

                if (data.length === 0) {
                    slotSelect.innerHTML += '<option>No Slots Available</option>';
                    return;
                }

                data.forEach(slot => {
                    let option = document.createElement("option");
                    option.value = slot.slot_id;
                    option.textContent = slot.slot_date + " - " + slot.slot_time;
                    slotSelect.appendChild(option);
                });

            })
            .catch(error => {
                console.error("Error loading slots:", error);
            });

    });

});