package org.stt.searching;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.stt.model.TimeTrackingItem;
import org.stt.persistence.ItemReader;
import org.stt.persistence.ItemReaderProvider;

import com.google.common.base.Optional;

public class DefaultItemSearcher implements ItemSearcher {

	private final ItemReaderProvider provider;

	/**
	 * @param reader
	 *            where to search for items
	 */
	public DefaultItemSearcher(ItemReaderProvider provider) {
		this.provider = checkNotNull(provider);
	}

	@Override
	public Collection<TimeTrackingItem> searchByStart(ReadableInstant from,
			ReadableInstant to) {

		Collection<TimeTrackingItem> foundElements = new LinkedList<>();
		Optional<TimeTrackingItem> item;
		try (ItemReader reader = provider.provideReader()) {
			while ((item = reader.read()).isPresent()) {
				TimeTrackingItem currentItem = item.get();
				if (!currentItem.getStart().isBefore(from)
						&& !currentItem.getStart().isAfter(to)) {
					foundElements.add(currentItem);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return foundElements;
	}

	// FIXME: incomplete and tests missing
	@Override
	public Collection<TimeTrackingItem> searchByEnd(ReadableInstant from,
			ReadableInstant to) {

		Collection<TimeTrackingItem> foundElements = new LinkedList<>();
		Optional<TimeTrackingItem> item;
		try (ItemReader reader = provider.provideReader()) {
			while ((item = reader.read()).isPresent()) {
				TimeTrackingItem currentItem = item.get();
				DateTime currentEnd = currentItem.getEnd().orNull();

				if (from == null && to == null && currentEnd == null) {
					foundElements.add(currentItem);

				} else if (from == null && currentEnd != null) { // NOPMD - not
																	// implemented
																	// yet

				} else if (to == null) { // NOPMD - not implemented yet

				} else if (!currentItem.getEnd().get().isBefore(from)
						&& !currentItem.getEnd().get().isAfter(to)) {
					foundElements.add(currentItem);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		throw new RuntimeException("not correctly implemented");
		// return foundElements;
	}

	@Override
	public Optional<TimeTrackingItem> getCurrentTimeTrackingitem() {
		try (ItemReader reader = provider.provideReader()) {
			Optional<TimeTrackingItem> item;
			TimeTrackingItem currentItem = null;
			while ((item = reader.read()).isPresent()) {
				currentItem = item.get();
			}
			return currentItem == null || currentItem.getEnd().isPresent() ? Optional
					.<TimeTrackingItem> absent() : Optional.of(currentItem);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<DateTime> getAllTrackedDays() {
		Collection<DateTime> result = new ArrayList<>();
		try (ItemReader reader = provider.provideReader()) {
			Optional<TimeTrackingItem> item;
			DateTime lastDay = null;
			while ((item = reader.read()).isPresent()) {
				DateTime currentDay = item.get().getStart()
						.withTimeAtStartOfDay();
				if (lastDay == null || !lastDay.equals(currentDay)) {
					result.add(currentDay);
					lastDay = currentDay;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
