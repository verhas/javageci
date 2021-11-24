# Java::Geci Annotations

This module contains only a few annotations.
The annotation `Geci` is usually used on classes that need the attention of code generators.
The annotation `Gecis` is used to make the annotation `Geci` repeatable.
The annotation `Generated` is used by many generators to signal that a class member is generated.

Note however, that Java::Geci is usually very flexible realizing annotations so long as long the name of the annotation matches.