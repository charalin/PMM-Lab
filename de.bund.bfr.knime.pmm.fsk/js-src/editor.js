metadata_editor = function () {

    var editor = {
	   version: "0.0.1"
    };
    editor.name = "FSK Metadata Editor";

    var _value;  // Raw FskMetadataEditorViewValue
    var _data;  // Only FskMetaData

    editor.init = function (representation, value)
    {
        // alert(JSON.stringify(value));

        _value = value;
        _data = value.metadata;
        create_body ();
    };

    editor.getComponentValue = function ()
    {
        return _value;
    };

    return editor;

    // --- utility functions ---
    function create_body ()
    {
        // Utility variables.
        // - Replace null strings with empty strings
        var createdDate = _data.createdDate === null ? "" : _data.createdDate;
        var modifiedDate = _data.modifiedDate === null ? "" : _data.modifiedDate;
        var rights = _data.rights === null ? "" : _data.rights;
        var notes = _data.notes === null ? "" : _data.notes;
        // curated is boolean: no need to assign it a default value

        var varTable =
            '<table class="table table-condensed">' +
            '  <tr>' +
            '    <th>Name</th>' +
            '    <th>Unit</th>' +
            '    <th>Type</th>' +
            '    <th>Value</th>' +
            '    <th>Min</th>' +
            '    <th>Max</th>' +
            '    <th>Dependent</th>'
            '  </tr>';
        // Row with dependent variable
        varTable +=
            '<tr>' +
            '  <td>' + _data.dependentVariable.name + '</td>' +
            '  <td>' + _data.dependentVariable.unit + '</td>' +
            '  <td>' + _data.dependentVariable.type + '</td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.value + '"></td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.min + '"></td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.max + '"></td>' +
            '  <td><input type="checkbox" class="form-control" checked disabled></td>' +
            '</tr>';
        // Row with independent variables
        for (var i = 0; i < _data.independentVariables.length; i++) {
            var variable = _data.independentVariables[i];
            varTable +=
                '<tr>' +
                '  <td>' + variable.name + '</td>' +
                '  <td>' + variable.unit + '</td>' +
                '  <td>' + variable.type + '</td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.value + '"></td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.min + '"></td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.max + '"></td>' +
                '  <td><input type="checkbox" class="form-control" disabled></td>' +
                '</tr>';
        }
        varTable += '</table>';

        var form = 
            '<form class="form-horizontal">' +

            // Model name form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modelName" class="col-sm-3 control-label">Model name</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.modelName === null ? "" : _data.modelName) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Model id form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modelId" class="col-sm-3 control-label">Model id</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.modelId === null ? "" : _data.modelId) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Model link form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modelLink" class="col-sm-3 control-label">Model link</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.modeLink === null ? "" : _data.modelLink) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Organism form
            '  <div class="form-group form-group-sm">' +
            '    <label for="organism" class="col-sm-3 control-label">Organism</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.organism === null ? "" : _data.organism) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Organism details form
            '  <div class="form-group form-group-sm">' +
            '    <label for="organismDetails" class="col-sm-3 control-label">Organism details</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.organismDetails === null ? "" : _data.organismDetails) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Matrix form
            '  <div class="form-group form-group-sm">' +
            '    <label for="matrix" class="col-sm-3 control-label">Matrix</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.matrix === null ? "" : _data.matrix) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Matrix details form
            '  <div class="form-group form-group-sm">' +
            '    <label for="matrixDetails" class="col-sm-3 control-label">Matrix details</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.matrixDetails === null ? "" : _data.matrixDetails) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Creator form
            '  <div class="form-group form-group-sm">' +
            '    <label for="creator" class="col-sm-3 control-label">Creator</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.creator === null ? "" : _data.creator) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Family name form
            '  <div class="form-group form-group-sm">' +
            '    <label for="familyName" class="col-sm-3 control-label">Family name</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.familyName === null ? "" : _data.familyName) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Contact form
            '  <div class="form-group form-group-sm">' +
            '    <label for="contact" class="col-sm-3 control-label">Contact</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.contact === null ? "" : _data.contact) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Software form
            '  <div class="form-group form-group-sm">' +
            '    <label for="software" class="col-sm-3 control-label">Software</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.software === null ? "" : _data.software) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Reference description form
            '  <div class="form-group form-group-sm">' +
            '    <label for="referenceDescription" class="col-sm-3 control-label">Referece description</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.referenceDescription === null ? "" : _data.referenceDescription) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Reference description link form
            '  <div class="form-group form-group-sm">' +
            '    <label for="referenceDescriptionLink" class="col-sm-3 control-label">Referece description link</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.referenceDescriptionLink === null ? "" : _data.referenceDescriptionLink) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Created date form
            '  <div class="form-group form-group-sm">' +
            '    <label for="createdDate" class="col-sm-3 control-label">Created date</label>' +
            '    <div class="col-sm-9">' +
            '      <input type="date" class="form-control" id="createdDateInput" placeholder="MM.dd.yyyy" value="' + createdDate + '">' +
            '      <span class="help-block">MM.dd.yyyy</span>' +
            '    </div>' +
            '  </div>' +

            // Modified date form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modifiedDate" class="col-sm-3 control-label">Modified date</label>' +
            '    <div class="col-sm-9">' +
            '      <input type="date" class="form-control" id="modifiedDateInput" placeholder="MM.dd.yyyy" value="' + modifiedDate + '">' +
            '      <span class="help-block">MM.dd.yyyy</span>' +
            '    </div>' +
            '  </div>' +

            // Rights form
            '  <div class="form-group form-group-sm">' +
            '    <label for="rights" class="col-sm-3 control-label">Rights</label>' +
            '      <div class="col-sm-9">' +
            '      <input type="text" class="form-control" id="rightsInput" value="' + rights + '">' +
            '    </div>' +
            '  </div>' +

            // Notes form
            '  <div class="form-group">' +
            '    <label for="notes" class="col-sm-3 control-label">Notes</label>' +
            '    <div class="col-sm-9">' +
            '      <textarea class="form-control" rows="3">' + notes + '</textArea>' +
            '    </div>' +
            '  </div>' +

            // Curated form
            '  <div class="form-group form-group-sm">' +
            '    <label for="curated" class="col-sm-3 control-label">Curated</label>' +
            '    <div class="col-sm-9">' +
            '      <input id="curatedInput" type="checkbox"' + (_data.curated ? " checked" : "") + '>' +
            '    </div>' +
            '  </div>' +

            // Model type form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modelType" class="col-sm-3 control-label">Model type</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.type === null ? "" : _data.type) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Model subject form
            '  <div class="form-group form-group-sm">' +
            '    <label for="modelSubject" class="col-sm-3 control-label">Model subject</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.subject === null ? "" : _data.subject) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Food process form
            '  <div class="form-group form-group-sm">' +
            '    <label for="foodProcess" class="col-sm-3 control-label">Food process</label>' +
            '    <div class="col-sm-9">' +
            '      <p class="form-control-static">' + (_data.foodProcess === null ? "" : _data.foodProcess) + '</p>' +
            '    </div>' +
            '  </div>' +

            // Has data form
            '  <div class="form-group form-group-sm">' +
            '    <label for="hasData" class="col-sm-3 control-label">Has data?</label>' +
            '    <div class="col-sm-9">' +
            '      <input id="hasDataInput" type="checkbox"' + (_data.hasData ? " checked" : "") + '>' +
            '    </div>' +
            '  </div>' +

            '</form>';

        var buttonDiv = 
            '<div class="col-sm-offset-3">' +
            '  <button id="resetButton" type="button" class="btn btn-warning">Reset</button>' +
            '  <button id="saveButton" type="button" class="btn btn-success">Save</button>' +
            '</div>';

        document.createElement("body");
        $("body").html('<div class="container">' + form + varTable + buttonDiv + '</div');

        $("#resetButton").click(reset);
        $("#saveButton").click(save);

        $("body div table tr:not(:first)").each(function(i, row) {
            $("td:eq(3) input", this).change(validateValueCell(i));
            $("td:eq(4) input", this).change(validateMinCell(i));
            $("td:eq(5) input", this).change(validateMaxCell(i));
        });
    }

    function validateValueCell(i) {
        // alert("validateValueCell");
    }

    function validateMinCell(i) {
        // alert("validateMinCell");
    }

    function validateMaxCell(i) {
        // alert("validateMaxCell");
    }

    function reset ()
    {
        $("#createdDateInput").val(_data.createdDate === null ? "" : _data.createdDate);
        $("#modifiedDateInput").val(_data.modifiedDate === null ? "" : _data.modifiedDate);
        $("#rightsInput").val(_data.rights === null ? "" : _data.rights);
        $("#notesInput").val(_data.notes === null ? "" : _data.notes);
        $("#curatedInput").prop("checked", _data.curated);
        $("#foodProcessInput").val(_data.foodProcess === null ? "" : _data.foodProcess);

        var table = $("body div table");
        table.find("tr:gt(0)").remove();

        // Row with dependent variable
        var depRow = 
            '<tr>' +
            '  <td>' + _data.dependentVariable.name + '</td>' +
            '  <td>' + _data.dependentVariable.unit + '</td>' +
            '  <td>' + _data.dependentVariable.type + '</td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.value + '"></td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.min + '"></td>' +
            '  <td><input type="number" class="form-control input-sm" value="' + _data.dependentVariable.max + '"></td>' +
            '  <td><input type="checkbox" class="form-control" checked disabled></td>' +
            '</tr>';
        table.append(depRow);

        // Rows with independent variables
        for (var i = 0; i < _data.independentVariables.length; i++) {
            var variable = _data.independentVariables[i];
            var indepRow =
                '<tr>' +
                '  <td>' + variable.name + '</td>' +
                '  <td>' + variable.unit + '</td>' +
                '  <td>' + variable.type + '</td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.value + '"></td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.min + '"></td>' +
                '  <td><input type="number" class="form-control input-sm" value="' + variable.max + '"></td>' +
                '  <td><input type="checkbox" class="form-control" ' + (variable.isDependent ? "checked" : "") + ' disabled></td>' +
                '</tr>';
            table.append(indepRow);
        }

        $("#hasDataInput").prop("checked", _data.hasData);
    }


    function save ()
    {
        // var hasErrors = false;

        // // Validate table
        // $("body div table tr:not(:first)").each(function() {

        //     alert("Validate table");

        //     var valueInput = $("td:eq(2) input", this);
        //     var minInput = $("td:eq(3) input", this);
        //     var maxInput = $("td:eq(4) input", this);

        //     // Check NaNs
        //     if (!$.isNumeric(valueInput.val())) {
        //        valueInput.addClass("has-error");
        //        hasErrors = true;
        //        return; 
        //     }

        //     if (!$.isNumeric(minInput.val())) {
        //         minInput.addClass("has-error");
        //         hasErrors = true;
        //         return;
        //     }

        //     if (!$.isNumeric(maxInput.val())) {
        //         maxInput.addClass("has-error");
        //         hasErrors = true;
        //         return;
        //     }

        //     var value = parseFloat(valueInput.val());
        //     var min = parseFloat(minInput.val());
        //     var max = parseFloat(maxInput.val());

        //     alert(value + " " + min + " " + max);

        //     // Validate min and max
        //     if (!(min < max)) {
        //         $("td:eq(3) input", this).addClass("has-error");
        //         hasErrors = true;
        //         return;
        //     }

        //     if (!(max > min)) {
        //         $("td:eq(4) input", this).addClass("has-error");
        //         hasErrors = true;
        //         return;
        //     }

        //     // Validate value
        //     if (isNaN(value) || value > min || value < max) {
        //         $("td:eq(2) input", this).text();
        //         hasErrors = true;
        //         return;
        //     }
        // });

        // if (hasErrors) {
        //     return;
        // }

        _data.createdDate = $("#createdDateInput").val();
        _data.modifiedDate = $("#modifiedDateInput").val();
        _data.rights = $("#rightsInput").val();
        _data.notes = $("#notesInput").val();
        _data.curated = $("#curatedInput").is(':checked');
        _data.foodProcess = $("#foodProcessInput").val();

        // Dependent variable
        var depRow = $("body div table tr:eq(1)");
        _data.dependentVariable.name = $("td:eq(0)", depRow).text();
        _data.dependentVariable.unit = $("td:eq(1)", depRow).text();
        _data.dependentVariable.type = $("td:eq(2)", depRow).text();
        _data.dependentVariable.value = $("td:eq(3) input", depRow).val();
        _data.dependentVariable.min = $("td:eq(4) input", depRow).val();
        _data.dependentVariable.max = $("td:eq(5) input", depRow).val();

        _data.independentVariables = []
        $("body div table tr:not(:first)").each(function() {
            var variable = {};
            variable.name = $("td:eq(0)", this).text();
            variable.unit = $("td:eq(1)", this).text();
            variable.type = $("td:eq(2)", this).text();
            variable.value = $("td:eq(3) input", this).val();
            variable.min = $("td:eq(4) input", this).val();
            variable.max = $("td:eq(5) input", this).val();
            _data.independentVariables.push(variable);
        });

        _data.hasData = $("#hasDataInput").is(':checked');

        _value.metadata = _data;  // Update metadata in ViewValue
    }
}();
