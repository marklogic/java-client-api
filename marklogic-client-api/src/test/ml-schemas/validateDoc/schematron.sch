<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron"
            queryBinding="xslt2" schemaVersion="1.0">
<sch:title>user-validation</sch:title>
<sch:phase id="phase1">
  <sch:active pattern="structural"></sch:active>
</sch:phase>
<sch:phase id="phase2">
  <sch:active pattern="co-occurence"></sch:active>
</sch:phase>
<sch:pattern id="structural">
  <sch:rule context="user">
    <sch:assert test="@id">user element must have an id attribute</sch:assert>
    <sch:assert test="count(*) = 5">
          user element must have 5 child elements: name, gender,
          age, score and result
    </sch:assert>
    <sch:assert test="score/@total">score element must have a total attribute</sch:assert>
    <sch:assert test="score/count(*) = 2">score element must have two child elements</sch:assert>
  </sch:rule>
</sch:pattern>
<sch:pattern id="co-occurence">
  <sch:rule context="score">
    <sch:assert test="@total = test-1 + test-2">
         total score must be a sum of test-1 and test-2 scores
    </sch:assert>
    <sch:assert test="(@total gt 30 and ../result = 'pass') or
              (@total le 30 and ../result = 'fail')" diagnostics="d1">
        if the score is greater than 30 then the result will be
        'pass' else 'fail'
    </sch:assert>
  </sch:rule>
</sch:pattern>
<sch:diagnostics>
<sch:diagnostic id="d1">the score does not match with the result</sch:diagnostic>
</sch:diagnostics>
</sch:schema>
