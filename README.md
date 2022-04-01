### Arch Unit

With Arch Units, we can test
- Package dependency checks
- Class dependency checks
- Class and package containment checks
- Inheritance checks
- Annotation checks
- Layer checks
- Cycle checks

I can think of adding numerous scenarios for test around codebase - 
- Interface should not reside in implementation package
- Naming Convention for methods/classes/variables !
- Cyclic Dependency
- Look for Multiple Implementations of a Interface, if they conflict.
- Ensure each path in controller has atleast unit test and integration test.
- Specific classes stay in necessary packages. Ex config class stays in config package.
- etc...

How It can be helpful ?
With Arch Units like dependency, I could abstract all the generic tests into a 
parent project which will ensure the developers follow the coding standards and code is inline
with standards agreed upon.

    If you have a large codebase, you should cache class file imports for your tests. 
    This step can greatly reduce the tests’ execution time. Caching of the imported classes, 
    for example, can be achieved with the ArchUnitRunner when you are using the JUnit support. 
    For small codebases, the reimport overhead of the classes is negligible.

