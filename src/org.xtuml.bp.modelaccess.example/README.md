This README is is the form of a mentor talking to a student.  It describes the 
purpose of this tool and teaches how to use it.   

===============================================================================   
Dear Student,   
 
One of my action items was to create a small example plugin that allows access 
to BridgePoint models in the workspace. Please find such an example attached.   
 
It is very small and thus easily understood. It contributes a top level menu 
entry and a tool button in the BridgePoint Utilities context menu named 
Model Statistics. When selected, the example accesses the models in the 
workspace and prints statistics on what it finds; the number of packages,
components, classes and state models.   
 
I recommend you look at StatsAction.java. The run method obtains a handle to 
the model database and then traverses to find the System Models (one per xtUML 
project). From there it traverses to the contained packages, their components,
the implementation packages inside the components, classes and state models 
before constructing a summary message.   
 
Note that metamodel classes are named using the architectural substitution 
variable '$Cr{class.Name}_c', so armed with the metamodel, you can predict the 
name of any java class you will need to access. Similarly, traversal is 
performed by static methods of the general form:    
<pre>
getMany<KeyLetter>sOnR<Association Number>(<starting point>)
</pre>.    
Note that 'starting point' may be a java instance or array of the appropriate 
type and 'KeyLetter' and 'Association Number' are obtained directly from the 
BridgePoint metamodel documentation.   
 
For example:   
 
```Package_c[] topLevelPkgs = Package_c.getManyEP_PKGsOnR1401(sysMdls);```   
 
is exactly equivalent to the OAL:   
 
```select many topLevelPkgs related by sysMdls->EP_PKG[R1401];```   
 
Note that by using these convenience methods, you automatically trigger the 
BridgePoint 'just-in-time' model data loading infrastructure, so you don't need 
to worry about what parts of the model have been loaded, BridgePoint takes care 
of that for you.   
 
As another example, metamodel attributes are accessed using the methods of the 
form:    
<pre>
get<AttributeName>(); 
</pre>
The return type being that specified as the data type of the attribute in the 
metamodel.   
 
I'm betting you can figure out other aspects of the model classes for yourself. 
I find that the Eclipse JDT code completion tool is very useful for quickly 
creating java code to traverse the model and extract the required data.   
 
Do please let me know how you get on with this exceedingly brief introduction. 
I'll be happy to follow up with additional examples as you require them. Also, 
I'd be delighted if you could share any cool plugins you develop with us and the
rest of the BridgePoint user base.   
 
 
very best regards,   
 
Your Mentor   
-----------------------   
Hi Mentor,   
 
Thank you so much for this information. I must say I'm really excited about 
this. I have been playing around with it for a bit.    
 
I can see there are six overloaded methods for each 
getMany<KeyLetter>sOnR<Association Number>(). I think I have figured out how the
second argument is intended to be used. Something like this would do the job:   
 
<pre>
Package_c [] pkgs = Package_c.getManyEP_PKGsOnR1401(sysMdl, new ClassQueryInterface_c() {
    public boolean evaluate(Object obj) {
        return ((Package_c)obj).getName().contains("myPackage");
    }
});
</pre>
 
Is this how you do it? Anyway, I don't have a clue what the third boolean 
argument is used for?   
 
The only obstacle I've found so far, is when you try to access the meta model at
start up of the workbench. The model elements doesn't seem to have been loaded 
at this point. I solved the problem by adding a modelChangeListener and wait for
them to become available. Is there some other criteria I could look for instead,
in order to know when the SystemModels are loaded? The reason I ask is because 
I have a view that is (could be) launched at start up, which is trying access 
the meta model, but it seem to be too early.    
 
However, I'm happy we came across this subject (instant meta model access) last 
week, I can see many interesting uses of this feature in the near future and I 
hope we get the opportunity to share them with you.   
  
Best Regards,   
 
Student   
--------------------------------------------------------------------------------   
Hi Student,    
 
Wow, I'm impressed :) I thought you might pick it up quickly, but _that_ 
quickly? Very cool!   
 
Yes, you have correctly inferred how to use the ClassQueryInterface_c. You will 
have seen that it is the internal infrastructure to support a 'where' clause. 
Good work.   
 
Not sure about the early loading. Now that I think about it, maybe I omitted to 
test the case where Model Explorer was not open at startup. I will see if there 
is a simpler way to ensure that the data always appears to be available, no 
matter how early you access it. I will get back to you if I find anything useful.   
 
In the short term, you seem to be off and running. Please do let me know what 
cool tools you build with this...    
 
very best regards,   
 
Mentor   
--------------------------------------------------------------------------------   
Hi again Student,   
 
The answer to your 'early launch' question is to add the following call in your 
startup code anywhere before you need to access model data:   

<pre> 
PersistenceManager.getDefaultInstance();
</pre>

This will force BridgePoint to do the required initialization synchronously, so 
you can go ahead and access the data immediately after this call. Sorry I 
missed this first time around...   
 
Don't worry about the last boolean in the interface, just use the API without 
it (a default true value is used). That boolean is used to disable just in time 
loading behavior when performing internal housekeeping. You will never want to 
do this.   

best regards,   
 
Your Mentor

