<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>README - University Server</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
            max-width: 800px;
            margin: auto;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #007bff;
        }

        .content {
            margin-top: 20px;
        }

        .alert {
            background-color: #ffdddd;
            border-left: 6px solid #f44336;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
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

        .readme-link {
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>
<h1>README for University Server</h1>
<div class="content">
    <p>Welcome to the University Server project! This project is a Java application for managing university data.</p>

    <h2>Using Postman</h2>
    <p>To facilitate testing the API, a Postman collection is provided. You can use this collection to make requests to the API and test its functionality. To do so:</p>
    <ol>
        <li>
            <p>Import the collection into Postman:</p>
            <a href="https://god.gw.postman.com/run-collection/35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D35343974-fc36830f-0e1a-4336-aafa-d22ed0b37080%26entityType%3Dcollection%26workspaceId%3D27ed7783-1773-466a-9f35-f723637494d0">
                <img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">
            </a>
        </li>
        <li>
            <p>Download the exported collection file:</p>
            <a href="https://github.com/AriiSib/university-server/blob/dev/University%20REST%20Application.postman_collection.json" class="button">Download Postman Collection</a>
        </li>
    </ol>
</div>
</body>
</html>
