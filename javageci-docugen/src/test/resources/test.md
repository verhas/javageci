# This is a test file that Markdown Snippet Inserter and Snippet collector will modify

[//]: (code brrra snippet="SnippetCollectorProcessExCode" number="start=1 step=1 format='%02d. ' from=0 to=-1")
```java
01.     @Override
02.     public void processEx(Source source) throws Exception {
03.         SnippetBuilder builder = null;
04.         for (final var line : source.getLines()) {
05.             final var starter = config.snippetStart.matcher(line);
06.             if (builder == null && starter.find()) {
07.                 builder = new SnippetBuilder(starter.group(1));
08.             } else if (builder != null) {
09.                 final var stopper = config.snippetEnd.matcher(line);
10.                 if (stopper.find()) {
11.                     snippets.put(builder.snippetName(), builder.build());
12.                     builder = null;
13.                 } else {
14.                     builder.add(line);
15.                 }
16.             }
17.         }
        }
```

[//]: (code brrb snippet="epsilon" append="snippets='SnippetAppender_.*,SnippetStore_name'")
```java
    @Override
    public void context(Context context) {
        super.context(context);
        fileNamePattern = Pattern.compile(config.files);
    }
    @Override
    protected void modify(Source source, Segment segment, Snippet snippet, CompoundParams params) {
        final var ph = new AtomicReference<Predicate<String>>(s -> false);
        Arrays.stream(params.get("snippets").split(","))
                .map(Pattern::compile)
                .map(Pattern::asMatchPredicate).forEach(
                       p -> ph.set(ph.get().or(p))
                );

        snippets.names().stream()
                .filter(ph.get())
                .sorted(String::compareTo)
                .map(n -> snippets.get(segment.sourceParams().id(), n))
                .forEach(s -> snippet.lines().addAll(s.lines()));
    }
```
