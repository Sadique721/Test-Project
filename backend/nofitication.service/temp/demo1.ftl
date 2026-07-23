<!DOCTYPE html>
<html>
<head>
    <title>Packet Attribute List</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
     rel="stylesheet" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
     crossorigin="anonymous">
    <link rel="stylesheet" href="css/style.css">
    <style>
        /* Add custom styles here */
        .table-bordered th, .table-bordered td {
            border: 1px solid #dee2e6;
        }
        .table-bordered th {
            background-color: #f8f9fa;
        }
        .tables-container {
            display: flex;
        }
        .table-container {
            flex: 1;
        }
        .table-container table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid #dee2e6;
        }
        .table-container th,
        .table-container td {
            text-align: left;
        }
        .truncate-text {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          width: 200px;
          border: 1px solid #ccc;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <h4>${firstName}</h4>
                <h4>Your packet attribute list is displayed below,</h4>
            </div>
            <div class="panel-body">
                <div class="tables-container">
                    <div class="table-container">
                        <table class="table table-striped table-bordered">
                            <thead>
                                <tr>
                                    <th>Label</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#list keys as key>
                                    <tr>
                                        <td class="truncate-text">
                                            ${key?default('N/A')}
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                    <div class="table-container">
                        <table class="table table-striped table-bordered">
                            <thead>
                                <tr>
                                    <th>Value</th>
                                </tr>
                            </thead>
                            <tbody>
                                <#list values as value>
                                    <tr>
                                        <td class="truncate-text">
                                            <#if value?has_content && value?trim?length != 0>${value}<#else>N/A</#if>
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
