<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>REST University Application</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
            max-width: 600px;
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

        .readme-link {
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>
<h1>Welcome to the REST University Application</h1>
<div class="content">
    <p>This application does not have a frontend interface.</p>
    <div class="alert">
        <strong>Important:</strong> To use this application, please use Postman to send requests to the API endpoints.
    </div>
    <p>All information about the API and usage instructions are provided in the
        <a href="<%= request.getContextPath() %>/README.md" class="readme-link">README.md</a> file.</p>
    <p>Alternatively, you can view the README file directly on our
        <a href="https://github.com/AriiSib/university-server.git" class="readme-link" target="_blank">GitHub
            repository</a>.</p>
</div>
</body>
</html>