package nl.jaapcoomans.boardgame.bgg;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import nl.jaapcoomans.boardgame.domain.GameRatingService;
import nl.jaapcoomans.boardgame.bgg.xmlapi.BigDecimalValue;
import nl.jaapcoomans.boardgame.bgg.xmlapi.ItemWithStatistics;
import nl.jaapcoomans.boardgame.bgg.xmlapi.Items;
import nl.jaapcoomans.boardgame.bgg.xmlapi.Statistics;
import nl.jaapcoomans.boardgame.bgg.xmlapi.Statistics.Ratings;
import nl.jaapcoomans.boardgame.domain.BoardGame;
import nl.jaapcoomans.boardgame.domain.BoardGameQueryRepository;

public class BoardGameGeekRatingService implements GameRatingService {
	private BoardGameGeekClient bggClient;
	private BoardGameQueryRepository boardGameQueryRepository;

	public BoardGameGeekRatingService(final BoardGameGeekClient bggClient, final BoardGameQueryRepository boardGameQueryRepository) {
		this.bggClient = bggClient;
		this.boardGameQueryRepository = boardGameQueryRepository;
	}

	@Override
	public Optional<BigDecimal> getAverageRating(final UUID gameId) {
		return this.resolveBoardGameGeekId(gameId)
			.map(bggId -> this.bggClient.getItemsWithRating(bggId))
			.map(Items::getItems)
			.map(List::stream)
			.flatMap(Stream::findFirst)
			.map(ItemWithStatistics::getStatistics)
			.map(Statistics::getRatings)
			.map(Ratings::getAverage)
			.map(BigDecimalValue::getValue);
	}

	private Optional<Integer> resolveBoardGameGeekId(UUID gameId) {
		return this.boardGameQueryRepository.findBoardGameById(gameId)
			.map(BoardGame::getBoardGameGeekId);
	}
}
