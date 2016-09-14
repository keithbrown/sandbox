package modelAccess.actions;

//
//  This simple action class is part of an example that demonstrates
//  java access to the BridgePoint model database.
//
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.xtuml.bp.core.ClassStateMachine_c;
import org.xtuml.bp.core.Component_c;
import org.xtuml.bp.core.InstanceStateMachine_c;
import org.xtuml.bp.core.ModelClass_c;
import org.xtuml.bp.core.Ooaofooa;
import org.xtuml.bp.core.Package_c;
import org.xtuml.bp.core.PackageableElement_c;
import org.xtuml.bp.core.SystemModel_c;
import org.xtuml.bp.core.util.UIUtil;

/**
 * This sample action accesses all BridgePoint models in the workspace and
 * traverses each to report the number of classes and state models in each
 * component.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class StatsAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public StatsAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		// Get the default model root and System Model instances for all the 
		//   projects in the workspace
		//  TODO - probably need an example also of how to get the current model root container of a selected NRME
		Ooaofooa def = Ooaofooa.getInstance("__default_root");
		SystemModel_c[] sysMdls = SystemModel_c.SystemModelInstances(def);

		// Get the top level packages inside this system
		Package_c[] topLevelPkgs = Package_c.getManyEP_PKGsOnR1401(sysMdls);
		String message = "";
		for (int i = 0; i < topLevelPkgs.length; i++) {
			// Get the components in this package (See the Packageable Element
			// package for these classes and traversals).
			// i.e. select many comps related by topLevelPkg[i]->PE_PE[R8001]->C_C[R8001]
			Component_c[] comps = Component_c
					.getManyC_CsOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(topLevelPkgs[i]));

			for (int j = 0; j < comps.length; j++) {
				message = message + "\nComponent '" + comps[j].getName() + "': ";
				// Now that we have a component, iterate over the packages it contains
				//   select many pkgs related by comp[i]->PE_PE[R8003]->EP_PKG[R8001]
				Package_c[] internalPkgs = Package_c
						.getManyEP_PKGsOnR8001(PackageableElement_c.getManyPE_PEsOnR8003(comps[j]));

				// Now get the classes inside the packages (just looking at the
				// top level, we're not recursing all nesting
				message = message + "Packages: " + internalPkgs.length + "\n";
				for (int k = 0; k < internalPkgs.length; k++) {
					// select many classes related by internalPkgs[k]->PE_PE[R8000]->O_OBJ[R8001]
					ModelClass_c[] classes = ModelClass_c
							.getManyO_OBJsOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(internalPkgs[k]));
					InstanceStateMachine_c[] isms = InstanceStateMachine_c.getManySM_ISMsOnR518(classes);
					ClassStateMachine_c[] csms = ClassStateMachine_c.getManySM_ASMsOnR519(classes);
					String classlist = "";
					String sep = "";
					for (int l = 0; l < classes.length; l++) {
						classlist = classlist + sep + classes[l].getName();
						sep = ", ";
					}
					message = message + "Package: " + internalPkgs[k].getName() + "\n";
					message = message + "Classes: " + classes.length + "\n";
					if ( classes.length != 0) {
					    message = message + "Instance State Machines: " + isms.length + " ";
					    message = message + "Class State Machines: " + csms.length + "\n";
					    message = message + "Classlist: " + classlist + "\n";
					}
				}
			}
		}

		UIUtil
		  .openInformation(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), "Example Model Access Plug-in",
				"Top level packages in workspace: " + topLevelPkgs.length + "\n" + message);
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
