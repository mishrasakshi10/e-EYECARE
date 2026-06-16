
function validateForm() {

    var name = document.getElementById("name").value.trim();
    var email = document.getElementById("email").value.trim();
    var phone = document.getElementById("phone").value.trim();
    var age = document.getElementById("age").value.trim();
    var password = document.getElementById("password").value;
    var confirm = document.getElementById("confirm").value;
    var gender = document.getElementById("gender").value;
    var role = document.getElementById("role").value;

    var error = document.getElementById("errorMsg");

    error.innerHTML = "";

    // -------- NAME VALIDATION --------
    var namePattern = /^[A-Za-z ]{3,50}$/;

    if (name == "") {
        error.innerHTML = "Name is required!";
        return false;
    }

    if (!namePattern.test(name)) {
        error.innerHTML = "Name must contain only alphabets (min 3 characters)!";
        return false;
    }

    // -------- EMAIL VALIDATION --------
    var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-z]{2,}$/;

    if (email == "") {
        error.innerHTML = "Email is required!";
        return false;
    }

    if (!emailPattern.test(email)) {
        error.innerHTML = "Enter a valid email address!";
        return false;
    }

    // -------- MOBILE VALIDATION --------
    var phonePattern = /^[0-9]{10}$/;

    if (phone == "") {
        error.innerHTML = "Phone number is required!";
        return false;
    }

    if (!phonePattern.test(phone)) {
        error.innerHTML = "Enter valid 10 digit phone number!";
        return false;
    }

    // -------- AGE VALIDATION --------
    if (age == "") {
        error.innerHTML = "Age is required!";
        return false;
    }

    if (isNaN(age) || age < 1 || age > 120) {
        error.innerHTML = "Enter valid age between 1 to 120!";
        return false;
    }

    // -------- PASSWORD VALIDATION --------
    /*
       Password must contain:
       - At least 8 characters
       - One uppercase letter
       - One lowercase letter
       - One number
       - One special character
    */

    var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

    if (password == "") {
        error.innerHTML = "Password is required!";
        return false;
    }

    if (!passwordPattern.test(password)) {
        error.innerHTML =
            "Password must be at least 8 characters with uppercase, lowercase, number & special character!";
        return false;
    }

    // -------- CONFIRM PASSWORD VALIDATION --------
    if (confirm == "") {
        error.innerHTML = "Confirm password is required!";
        return false;
    }

    if (password !== confirm) {
        error.innerHTML = "Passwords do not match!";
        return false;
    }

    // -------- GENDER VALIDATION --------
    if (gender == "") {
        error.innerHTML = "Please select gender!";
        return false;
    }

    // -------- ROLE VALIDATION --------
    if (role == "") {
        error.innerHTML = "Please select user type!";
        return false;
    }

    // If everything is correct
    return true;
}
