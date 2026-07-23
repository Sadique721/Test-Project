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
            .table-bordered th, .table-bordered td {
                border: 1px solid #dee2e6; /* Add border to all table cells */
                padding: 0.2rem; /* Adjust cell padding */
            }

            .table-bordered th {
                background-color: #f8f9fa;
            }

            /* Remove spacing between columns */
            .table-bordered th:not(:last-child),
            .table-bordered td:not(:last-child) {
                border-right: none;
            }

            /* Remove extra spacing in the first and last cell of each row */
            .table-bordered tr:first-child th,
            .table-bordered tr:first-child td,
            .table-bordered tr:last-child th,
            .table-bordered tr:last-child td {
                border-bottom: none;
            }

            /* Remove extra spacing in the first and last row of the table */
            .table-bordered thead:first-child tr:first-child th,
            .table-bordered tbody:last-child tr:last-child td {
                border-bottom: 1px solid #dee2e6;
            }

            /* Remove extra spacing between table and container */
            .container table {
                margin-bottom: 0;
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
                    <table class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>Header</th>
                                <th>Content</th>
                            </tr>
                        </thead>
                        <tbody>
                            <#list filteredAttributes as attribute>
                                <tr>
                                    <td>${(attribute.header)!"N/A"}</td>
                                    <td>${(attribute.content)!"N/A"}</td>
                                </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
