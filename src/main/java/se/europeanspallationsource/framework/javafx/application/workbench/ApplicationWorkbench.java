/*
 * Copyright 2018 European Spallation Source ERIC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.framework.javafx.application.workbench;


import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jacpfx.api.annotations.workbench.Workbench;
import org.jacpfx.api.componentLayout.WorkbenchLayout;
import org.jacpfx.api.message.Message;
import org.jacpfx.api.util.ToolbarPosition;
import org.jacpfx.rcp.componentLayout.FXComponentLayout;
import org.jacpfx.rcp.components.menuBar.JACPMenuBar;
import org.jacpfx.rcp.workbench.FXWorkbench;
import se.europeanspallationsource.framework.javafx.application.utilities.Bundles;

import static se.europeanspallationsource.framework.javafx.application.workbench.Constants.PERSPECTIVE_MAIN;


/**
 * The application workbench, where perspectives are registered and installed.
 * <p>
 * The complete application is made of 6 view areas, where the <i>main</i> one is
 * always visible.
 * </p>
 *
 * <pre>
 * ┌───────────────────┬───────────────────────────────────────┬──────────────────┐
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         B         │                                       │                  │
 * │      browser      │                                       │                  │
 * │                   │                                       │        P         │
 * │                   │                                       │     palette      │
 * │                   │                   M                   │                  │
 * ├───────────────────┤                 main                  │                  │
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │                   │                                       ├──────────────────┤
 * │                   │                                       │                  │
 * │                   │                                       │                  │
 * │         N         │                                       │                  │
 * │     navigator     │                                       │        I         │
 * │     overview      ├───────────────────────────────────────┤    inspector     │
 * │                   │                                       │    properties    │
 * │                   │                   C                   │                  │
 * │                   │                console                │                  │
 * │                   │               messages                │                  │
 * │                   │                                       │                  │
 * └───────────────────┴───────────────────────────────────────┴──────────────────┘
 * </pre>
 * 
 * <h3>Outer View Areas</h3>
 *
 * <p>
 * A view area is visible only if at least one view is registered. The only
 * exception is the <i>main</i> area, where views can be added dynamically, and
 * no initial view is required.
 * </p>
 *
 * <h4>Browser View Area</h4>
 *
 * <p>
 * The <i>browser</i> view should be used to navigate high-level structures in
 * order to find elements to be opened in dedicated views inside the <i>main</i>
 * area.
 * </p>
 *
 * <h4>Navigator/Overview View Area</h4>
 *
 * <p>
 * This view area should be used to navigate the content of <i>selected</i>
 * entities, or display an overview of a more detailed zone displayed in
 * the currently visible <i>main</i> view.
 * </p>
 *
 * <h4>Console/Messages View Area</h4>
 *
 * <p>
 * The <i>console</i> view area should be used to display messages from the
 * application to the users, mirroring, for example, the standard {@link System#out}
 * and {@link System#err} streams.
 * </p>
 *
 * <h4>Palette View Area</h4>
 *
 * <p>
 * The <i>palette</i> view area is where to display a set of (draggable or
 * selectable) elements frequently used by the user.
 * </p>
 *
 * <h4>Inspector/Properties View Area</h4>
 *
 * <p>
 * This area should be used to display details of the element selected in the
 * currently visible <i>main</i> view. Usually a table of name/value pairs is
 * used to list the properties of the selected element, but other visualizations
 * are possible too.
 * </p>
 *
 * <h3>Main View Area</h3>
 *
 * <p>
 * The <i>main</i> view area will contains one or more views (not necessarily
 * "document-based") showing the data the use will act upon. The views in the
 * <i>main</i> area can be opened from user's interaction in the <i>browser</i>
 * and/or the <i>navigation</i> area, or programmatically.
 * </p>
 *
 * <h3>Perspectives</h3>
 *
 * <p>
 * The 32 different and possible configuration allowed by the framework are the
 * following (18 require the whole space be divided in 3 columns, 12 in 2 column,
 * and the remaining 2 don't require any vertical split):
 * </p>
 *
 * <pre>
 * ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐
 * │  B  │       │     │ │  B  │       │     │ │  B  │       │     │ │  B  │       │     │ │     │       │     │ │     │       │     │
 * ├─────┤       │  P  │ ├─────┤       │     │ ├─────┤       │     │ ├─────┤       │  P  │ │     │       │  P  │ │     │       │  P  │
 * │     │   M   │     │ │     │   M   │     │ │     │   M   │     │ │     │       │     │ │     │   M   │     │ │     │   M   │     │
 * │     │       ├─────┤ │     │       │  I  │ │     │       │  P  │ │     │   M   ├─────┤ │  B  │       ├─────┤ │  N  │       ├─────┤
 * │  N  │       │     │ │  N  │       │     │ │  N  │       │     │ │  N  │       │     │ │     │       │     │ │     │       │     │
 * │     ├───────┤  I  │ │     ├───────┤     │ │     ├───────┤     │ │     │       │  I  │ │     ├───────┤  I  │ │     ├───────┤  I  │
 * │     │   C   │     │ │     │   C   │     │ │     │   C   │     │ │     │       │     │ │     │   C   │     │ │     │   C   │     │
 * └─────┴───────┴─────┘ └─────┴───────┴─────┘ └─────┴───────┴─────┘ └─────┴───────┴─────┘ └─────┴───────┴─────┘ └─────┴───────┴─────┘
 *                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐
 *                       │  B  │       │     │ │  B  │       │     │                       │     │       │     │ │     │       │     │
 *                       ├─────┤       │     │ ├─────┤       │     │                       │     │       │  P  │ │     │       │  P  │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │   M   │  I  │ │     │   M   │  P  │                       │  B  │   M   ├─────┤ │  N  │   M   ├─────┤
 *                       │  N  │       │     │ │  N  │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │  I  │ │     │       │  I  │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       └─────┴───────┴─────┘ └─────┴───────┴─────┘                       └─────┴───────┴─────┘ └─────┴───────┴─────┘
 *                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │   M   │     │ │     │   M   │     │                       │     │   M   │     │ │     │   M   │     │
 *                       │  N  │       │  I  │ │  N  │       │  P  │                       │  B  │       │  I  │ │  B  │       │  P  │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     ├───────┤     │ │     ├───────┤     │                       │     ├───────┤     │ │     ├───────┤     │
 *                       │     │   C   │     │ │     │   C   │     │                       │     │   C   │     │ │     │   C   │     │
 *                       └─────┴───────┴─────┘ └─────┴───────┴─────┘                       └─────┴───────┴─────┘ └─────┴───────┴─────┘
 *                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐                       ┌─────┬───────┬─────┐ ┌─────┬───────┬─────┐
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │  N  │   M   │  I  │ │  N  │   M   │  P  │                       │  B  │   M   │  I  │ │  B  │   M   │  P  │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       │     │       │     │ │     │       │     │                       │     │       │     │ │     │       │     │
 *                       └─────┴───────┴─────┘ └─────┴───────┴─────┘                       └─────┴───────┴─────┘ └─────┴───────┴─────┘
 * ┌─────────────┬─────┐ ┌─────────────┬─────┐ ┌─────────────┬─────┐ ┌─────────────┬─────┐ ┌─────────────┬─────┐ ┌─────────────┬─────┐
 * │             │     │ │             │     │ │             │     │ │             │     │ │             │     │ │             │     │
 * │             │  P  │ │             │     │ │             │     │ │             │  P  │ │             │     │ │             │     │
 * │      M      │     │ │      M      │     │ │      M      │     │ │             │     │ │             │     │ │             │     │
 * │             ├─────┤ │             │  I  │ │             │  P  │ │      M      ├─────┤ │      M      │  I  │ │      M      │  P  │
 * │             │     │ │             │     │ │             │     │ │             │     │ │             │     │ │             │     │
 * ├─────────────┤  I  │ ├─────────────┤     │ ├─────────────┤     │ │             │  I  │ │             │     │ │             │     │
 * │      C      │     │ │      C      │     │ │      C      │     │ │             │     │ │             │     │ │             │     │
 * └─────────────┴─────┘ └─────────────┴─────┘ └─────────────┴─────┘ └─────────────┴─────┘ └─────────────┴─────┘ └─────────────┴─────┘
 * ┌─────┬─────────────┐ ┌─────┬─────────────┐ ┌─────┬─────────────┐ ┌─────┬─────────────┐ ┌─────┬─────────────┐ ┌─────┬─────────────┐
 * │  B  │             │ │     │             │ │     │             │ │  B  │             │ │     │             │ │     │             │
 * ├─────┤             │ │     │             │ │     │             │ ├─────┤             │ │     │             │ │     │             │
 * │     │      M      │ │     │      M      │ │     │      M      │ │     │             │ │     │             │ │     │             │
 * │     │             │ │  B  │             │ │  N  │             │ │     │      M      │ │  B  │      M      │ │  N  │      M      │
 * │  N  │             │ │     │             │ │     │             │ │  N  │             │ │     │             │ │     │             │
 * │     ├─────────────┤ │     ├─────────────┤ │     ├─────────────┤ │     │             │ │     │             │ │     │             │
 * │     │      C      │ │     │      C      │ │     │      C      │ │     │             │ │     │             │ │     │             │
 * └─────┴─────────────┘ └─────┴─────────────┘ └─────┴─────────────┘ └─────┴─────────────┘ └─────┴─────────────┘ └─────┴─────────────┘
 * ┌───────────────────┐ ┌───────────────────┐
 * │                   │ │                   │
 * │                   │ │                   │
 * │         M         │ │                   │
 * │                   │ │         M         │
 * │                   │ │                   │
 * ├───────────────────┤ │                   │
 * │         C         │ │                   │
 * └───────────────────┘ └───────────────────┘
 * </pre>
 * 
 * <p>
 * ...
 * </p>
 *
 * @author claudio.rosati@esss.se
 */
@Workbench(
     id = "application.workbench",
     perspectives = { PERSPECTIVE_MAIN }
)
public class ApplicationWorkbench implements FXWorkbench {

    @Override
    public void handleInitialLayout( Message<Event, Object> action, WorkbenchLayout<Node> layout, Stage stage ) {

//  TODO: CR: Initial size should be declared through the ApplicationLauncher
//            constructor's parameters, or using an @ApplicationInfo annotation.
//            This values should then be saved into application properties, and
//            updated when the application closes.
        layout.setWorkbenchXYSize(1024, 768);
        layout.registerToolBar(ToolbarPosition.NORTH);
        layout.setStyle(StageStyle.DECORATED);
        layout.setMenuEnabled(true);
        
    }

    @Override
    public void postHandle( FXComponentLayout layout ) {

        final JACPMenuBar menu = layout.getMenu();

//  TODO:CR menus should be loaded dynamically, using a service.
        Menu fileMenu = new Menu(Bundles.getLocalizedStrings().getString("menubar.file"));

        menu.getMenus().addAll(fileMenu);

    }

}
