import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import javax.swing.*

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val app = App()                 // Get an app state object
    val window = MainWindow(app)    // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }
}


/**
 * Manage app state
 *
 * @property name the user's name
 * @property score the points earned
 */
class App {
    var name = "Kill The Human"
    var score = 0

    fun scorePoints(points: Int) {
        score += points
    }

    fun resetScore() {
        score = 0
    }

    fun maxScoreReached(): Boolean {
        return score >= 10000
    }
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param app the app state object
 */
class MainWindow(val app: App) {
    val frame = JFrame("Kill the Human")
    private val panel = JPanel().apply { layout = null }
    private val mapPanel = JPanel()
    private val titleLabel = JLabel("APP TITLE")

    private val infoLabel = JLabel()
    private val clickButton = JButton("Click Me!")
    private val infoButton = JButton("Info")

    private val mapWindow = MapWindow(this, app)      // Pass app state to dialog too

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(720, 480)
        titleLabel.setBounds(30, 30, 340, 30)
        infoLabel.setBounds(30, 90, 340, 30)
        clickButton.setBounds(30, 150, 240, 40)
        infoButton.setBounds(300, 150, 70, 40)

        panel.add(mapPanel)
        panel.add(titleLabel)
        panel.add(infoLabel)
        panel.add(clickButton)
        panel.add(infoButton)
    }

    private fun setupStyles() {
        titleLabel.font = Font(Font.SANS_SERIF, Font.BOLD, 32)
        infoLabel.font = Font(Font.SANS_SERIF, Font.PLAIN, 20)
        mapPanel.background = Color.RED
        clickButton.font = Font(Font.SANS_SERIF, Font.PLAIN, 20)
        clickButton.background = Color(0xFF0055)

        infoButton.font = Font(Font.SANS_SERIF, Font.PLAIN, 20)
    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {
        clickButton.addActionListener { handleMainClick() }
        infoButton.addActionListener { handleInfoClick() }
    }

    private fun handleMainClick() {
        app.scorePoints(1000)       // Update the app state
        updateUI()                  // Update this window UI to reflect this
    }

    private fun handleInfoClick() {
        mapWindow.show()
    }

    fun updateUI() {
        infoLabel.text = "User ${app.name} has ${app.score} points"

        if (app.maxScoreReached()) {
            clickButton.text = "No More!"
            clickButton.isEnabled = false
        } else {
            clickButton.text = "Click Me!"
            clickButton.isEnabled = true
        }

        mapWindow.updateUI()       // Keep child dialog window UI up-to-date too
    }

    fun show() {
        frame.isVisible = true
    }
}


/**
 * Info UI window is a child dialog and shows how the
 * app state can be shown / updated from multiple places
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param app the app state object
 */
class MapWindow(val owner: MainWindow, val app: App) {
    private val dialog = JDialog(owner.frame, "DIALOG TITLE", false)
    private val panel = JPanel().apply { layout = null }

    private val infoLabel = JLabel()

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(240, 180)

        infoLabel.setBounds(30, 30, 180, 60)

        panel.add(infoLabel)
    }

    private fun setupStyles() {
        infoLabel.font = Font(Font.SANS_SERIF, Font.PLAIN, 16)
    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    private fun setupActions() {
    }

    fun updateUI() {
        // Use app properties to display state
        infoLabel.text = "<html>User: ${app.name}<br>Score: ${app.score} points"
    }

    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y
        )

        dialog.isVisible = true
    }
}