# This is a test file that Markdown Snippet Inserter and Snippet collector will modify

[//]: (code brrra snippet="SnippetCollectorProcessExCode" number="start=1 step=2 format='%03d. ' lines='1:'")
```java
    @Override
    public void processEx(Source source) throws Exception {
        SnippetBuilder builder = null;
        for (final var line : source.getLines()) {
            final var starter = config.snippetStart.matcher(line);
            if (builder == null && starter.find()) {
                builder = new SnippetBuilder(starter.group(1));
            } else if (builder != null) {
                final var stopper = config.snippetEnd.matcher(line);
                if (stopper.find()) {
                    snippets.put(builder.snippetName(), builder.build());
                    builder = null;
                } else {
                    builder.add(line);
                }
            }
        }
    }
```
