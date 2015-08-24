package main.java;

import main.java.util.Persister;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@RunWith(MockitoJUnitRunner.class)
public class ProducerTest {

	@Mock
	private Persister persister;

	@Test
	public void shouldPutTagToQueue() throws Exception {
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(10);

		final String command = "tag \nexit";

		final Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
		final Thread producer = new Thread(new Producer(persister, queueTags, scanner));

		producer.start();
		producer.join();

		Assert.assertTrue("Queue doesn't contain injection after exit", queueTags.contains("shutdown"));
		Assert.assertEquals("Wrong queue size", 2, queueTags.size());
		Assert.assertEquals("Queue has got the wrong tag", "tag", queueTags.take());

	}

	@Test
	public void shouldPutSomeTagsToQueueWithWhitespace() throws Exception {
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(10);

		final String command = "tag1 tag2 tag3 \nexit";

		final Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
		final Thread producer = new Thread(new Producer(persister, queueTags, scanner));

		producer.start();
		producer.join();

		Assert.assertTrue("Queue doesn't contain injection after exit", queueTags.contains("shutdown"));
		Assert.assertEquals("Wrong queue size", 4, queueTags.size());
		Assert.assertTrue("Queue doesn't contain tag1", queueTags.contains("tag1"));
		Assert.assertTrue("Queue doesn't contain tag2", queueTags.contains("tag2"));
		Assert.assertTrue("Queue doesn't contain tag3", queueTags.contains("tag3"));
	}

	@Test
	public void shouldPutSomeTagsToQueueWithEnterKey() throws Exception {
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(10);

		final String command = "tag1 \ntag2 \ntag3 \nexit";

		final Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
		final Thread producer = new Thread(new Producer(persister, queueTags, scanner));

		producer.start();
		producer.join();

		Assert.assertTrue("Queue doesn't contain injection after exit", queueTags.contains("shutdown"));
		Assert.assertEquals("Wrong queue size", 4, queueTags.size());
		Assert.assertTrue("Queue doesn't contain tag1", queueTags.contains("tag1"));
		Assert.assertTrue("Queue doesn't contain tag2", queueTags.contains("tag2"));
		Assert.assertTrue("Queue doesn't contain tag3", queueTags.contains("tag3"));
	}

	@Test
	public void shouldExit() throws Exception {
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(10);

		final String command = "exit";

		final Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
		final Thread producer = new Thread(new Producer(persister, queueTags, scanner));

		producer.start();
		producer.join();

		Assert.assertTrue("Queue doesn't contain injection after exit", queueTags.contains("shutdown"));
		Assert.assertEquals("Wrong queue size", 1, queueTags.size());
	}

	@Test
	public void should() throws Exception {
		final BlockingQueue<String> queueTags = new ArrayBlockingQueue<String>(10);

		final String command = "get statistics\nexit";

		final Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
		final Thread producer = new Thread(new Producer(persister, queueTags, scanner));

		List<String> tags = new ArrayList<>(3);
		tags.add("tag1");
		tags.add("tag2");
		tags.add("tag3");
		Mockito.when(persister.getLastWeekTag()).thenReturn(tags);

		tags = new ArrayList<>(1);
		tags.add("tag1");
		Mockito.when(persister.getTheMostPopularTag()).thenReturn(tags);

		producer.start();
		producer.join();

		Mockito.verify(persister).getLastWeekTag();
		Mockito.verify(persister).getTheMostPopularTag();
		Assert.assertTrue("Queue doesn't contain injection after exit", queueTags.contains("shutdown"));
		Assert.assertEquals("Wrong queue size", 1, queueTags.size());
	}


}