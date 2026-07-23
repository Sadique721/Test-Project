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
        </style>
    </head>
    <body>
        <div class="container">
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <h4>${firstName}</h4>
                    <h4>Packet Attribute List</h4>
                </div>
                <div class="panel-body">
                    <table class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>Label</th>
                                <th>Name</th>
                                <th>index</th>
                                <th>dataType</th>
                                <th>dateFormat</th>
                                <th>regex</th>
                                <th>regexGroupIndex</th>
                                <th>value</th>
                                <th>targetDateFormat</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list filteredAttributes as attribute>
                                <tr>
                                    <td>${attribute.label?default('N/A')}</td>
                                    <td>${attribute.name?default('N/A')}</td>
                                    <td>${attribute.index?default('N/A')}</td>
                                    <td>${attribute.dataType?default('N/A')}</td>
                                    <td>${attribute.dateFormat?default('N/A')}</td>
                                    <td>${attribute.regex?default('N/A')}</td>
                                    <td>${attribute.regexGroupIndex?default('N/A')}</td>
                                    <td>${attribute.value?default('N/A')}</td>
                                    <td>${attribute.targetDateFormat?default('N/A')}</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
