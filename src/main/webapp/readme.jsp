<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>University Server</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            margin: 20px;
        }
        .button {
            display: inline-block;
            padding: 10px 20px;
            font-size: 16px;
            color: #fff;
            background-color: #007bff;
            text-decoration: none;
            border-radius: 5px;
        }
        .button:hover {
            background-color: #0056b3;
        }
        .container {
            max-width: 800px;
            margin: auto;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Проект "University Server"</h1>
    <p>Добро пожаловать в проект "University Server"! Этот проект представляет собой Java-приложение для управления университетскими данными.</p>

    <h2>Использование Postman</h2>
    <p>Для удобства тестирования API предоставлена коллекция Postman. Вы можете использовать коллекцию для выполнения запросов к API и проверки функциональности. Для этого:</p>
    <ol>
        <li>
            <p>Импортируйте коллекцию в Postman:</p>
            <a href="https://god.gw.postman.com/run-collection/35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080%26entityType%3Dcollection%26workspaceId%3D27ed7783-1773-466a-9f35-f723637494d0">
                <img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">
            </a>
        </li>
        <li>
            <p>Экспортированный файл коллекции:</p>
            <a href="${request.getAttribute('collectionFileUrl')}" class="button">Скачать коллекцию Postman</a>
        </li>
    </ol>

    <h2>Инструкции по запуску проекта</h2>
    <ol>
        <li>Убедитесь, что у вас установлены все необходимые зависимости (Java, Tomcat, Gradle и т.д.).</li>
        <li>Соберите и разверните проект, следуя инструкциям в проектной документации.</li>
        <li>Используйте Postman для тестирования API, следуя инструкциям в коллекции.</li>
    </ol>
    <p>Если у вас возникнут вопросы или проблемы, пожалуйста, свяжитесь с командой разработки.</p>
</div>
</body>
</html>
