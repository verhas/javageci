# This is a test file that Markdown Snippet Inserter and Snippet collector will modify


[//]: # (snip brrra snippet="SnippetCollectorProcessExCode" trim="yes" number="start=1 step=1 format='%02d. ' from=0 to=-1" regex="replace='/^~s{2}/ /' tilde=true" skip="do")
```java
01. 01. 01. 01. 01. 01. 01. 01. @Override
02. 02. 02. 02. 02. 02. 02. 02. public void processEx(Source source) throws Exception {
03. 03. 03. 03. 03. 03. 03. 03. SnippetBuilder builder = null;
04. 04. 04. 04. 04. 04. 04. 04. for (final var line : source.getLines()) {
05. 05. 05. 05. 05. 05. 05. 05.    final var starter = config.snippetStart.matcher(line);
06. 06. 06. 06. 06. 06. 06. 06.    if (builder == null && starter.find()) {
07. 07. 07. 07. 07. 07. 07. 07.        builder = new SnippetBuilder(starter.group(1));
08. 08. 08. 08. 08. 08. 08. 08.    } else if (builder != null) {
09. 09. 09. 09. 09. 09. 09. 09.        final var stopper = config.snippetEnd.matcher(line);
10. 10. 10. 10. 10. 10. 10. 18.    }
11. 11. 11. 11. 11. 11. 11. 19. }
12. 12. 12. 12. 12. 12. 12. 20. if (builder != null) {
13. 13. 13. 13. 13. 13. 13. 21.    throw new GeciException("Snippet " + builder.snippetName() + " was not finished before end of the file " + source.getAbsoluteFile());
14. 14. 14. 14. 14. 14. 14. 22. }
                                }
```

<!-- snip bizerba snippet="SnippetStore_name" -->
    Set<String> names() {
        return originals.keySet();
    }
<!-- end snip -->

[//]: # (snip brrb snippet="epsilon" append="snippets='SnippetAppender_.*,SnippetStore_name'")
```java
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var segmentName = segment.sourceParams().id();
        final var namePatterns = Arrays.stream(params.get("snippets").split(","))
            .map(Pattern::compile)
            .map(Pattern::asMatchPredicate).collect(Collectors.toList());

        for (final var pattern : namePatterns) {
            snippets.names().stream()
                .filter(pattern)
                .sorted(String::compareTo)
                .map(name -> snippets.get(segmentName, name))
                .forEach(snip -> snippet.lines().addAll(snip.lines()));
        }
    }
    Set<String> names() {
        return originals.keySet();
    }
```
