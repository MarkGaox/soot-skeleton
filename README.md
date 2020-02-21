# soot-skeleton
Automatically enumerates some options in a Soot based on user determined example output and input and generates a starter script.

Clone/Symlink the `android-platforms` folder (https://github.com/izgzhen/android-platforms):

```
ln -s path/to/android-platforms .
```
Build up project:
```
make
```
For more resources, please check wiki.

## Example

TODO: explain more about these configuration's content.

### Commandline Options
```
Runner Mode Options
    -r arg1 arg2:
        This Option indicates that you want to use the runner mode of SootSkeleton.
        And you need to pass two parameter. 
    -cg : This is the call graph indicator.
    -rf : THis is the reaching definition indicator. 
    
    Note:
        And you have to add one options from "-cg" and "-rf" to indicate whether you
        want to run the call graph analysis or the reaching definition analysis.
          
Generator Mode Options:
    -cfg arg1:
        This is the option to indicate the path towards the configuration(confige.yaml as describing in next part).
    -exp arg1:
        This is the option to the path towards the indicative examples.
        
     Note:
        Both "-cfg arg1" and "-exp arg1" should be included for generator to perform correctly.   
```

```
This is the general form for the config.yaml (those lines that start with "#" are all comments, you can ignore it whe
when write your own files)

# To indicate whether input is an apk or a java source file.
apk: "false"
javaClass: "true"
# To indicate the target file is in which folder.
pathToTargetDirectory: "test-resource"
# This options stands for the class name of the java target.
className: "DemoClass"
# The ouptut path for the generated configuration.
outputPath: "result2.yaml"

```