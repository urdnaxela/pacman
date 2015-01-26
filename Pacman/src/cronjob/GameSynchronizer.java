package cronjob;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pacman.PMF;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class GameSynchronizer extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static volatile int TASK_COUNTER = 0;
	private static final Logger _logger = Logger
			.getLogger(GameSynchronizer.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse response)
			throws IOException {

		// This is the body of the task
		_logger.info("Processing: " + req.getHeader("X-AppEngine-TaskName")
				+ "-" + TASK_COUNTER++);
		queueObjectsNeedingProcessing();
		try {

			// Sleep for a second (if the rate is set to 1/s this will allow
			// at most 1 more task to be processed)
			Thread.sleep(1000);

		} catch (InterruptedException e) { // ignore}

		}

	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
	}

	static public int queueObjectsNeedingProcessing() {
		int count = 0;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Queue queue = QueueFactory.getQueue("synchonizeTask");
		try {
			pm.currentTransaction().begin();

			TaskOptions taskOptions = TaskOptions.Builder.withUrl(
					"/GameSynchronizer").method(TaskOptions.Method.GET);
			queue.add(taskOptions);
			pm.currentTransaction().commit();
			return count;
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
		}
	}
}