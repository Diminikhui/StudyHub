<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $to = 'dimachemerkinkek@gmail.com'; // Замените на свой адрес электронной почты

    $name = $_POST['name'];
    $email = $_POST['email'];
    $message = $_POST['message'];

    $subject = 'Новое сообщение с формы обратной связи';

    $headers = "From: $name <$email>\r\n";
    $headers .= "Reply-To: $email\r\n";
    
    $email_content = "Имя: $name\n";
    $email_content .= "Email: $email\n\n";
    $email_content .= "Сообщение:\n$message\n";

    mail($to, $subject, $email_content, $headers);

    http_response_code(200);
    echo "Спасибо! Ваше сообщение было успешно отправлено.";
} else {
    http_response_code(403);
    echo "Ошибка при отправке формы. Пожалуйста, попробуйте еще



    



