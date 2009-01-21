package de.ralfebert.rcputils.handler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

/**
 * Use this handler for style="toggle" command contributions. You need to
 * declare a state for your command to use ToggleHandler:
 * 
 * <pre>
 * <command id="somecommand" name="SomeCommand">
 * 	 <state class="org.eclipse.jface.commands.ToggleState" id="STYLE"/>
 * </command>
 * </pre>
 * 
 * The id="STYLE" was chosen because of IMenuStateIds.STYLE - maybe this will
 * work without any Handler foo in later Eclipse versions.
 * 
 * @author Ralf Ebert
 */
public abstract class ToggleHandler extends AbstractHandler implements IElementUpdater {

	private String commandId;

	public final Object execute(ExecutionEvent event) throws ExecutionException {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		this.commandId = event.getCommand().getId();

		State state = event.getCommand().getState(IMenuStateIds.STYLE);
		if (state == null)
			throw new ExecutionException(
					"You need to declare a ToggleState with id=STYLE for your command to use ToggleHandler!");
		boolean currentState = (Boolean) state.getValue();
		boolean newState = !currentState;
		state.setValue(newState);
		executeToggle(event, newState);
		commandService.refreshElements(event.getCommand().getId(), null);

		// return value is reserved for future apis
		return null;
	}

	protected abstract void executeToggle(ExecutionEvent event, boolean newState);

	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		if (this.commandId != null) {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
					ICommandService.class);
			Command command = commandService.getCommand(commandId);
			element.setChecked((Boolean) command.getState(IMenuStateIds.STYLE).getValue());
		}
	}

}