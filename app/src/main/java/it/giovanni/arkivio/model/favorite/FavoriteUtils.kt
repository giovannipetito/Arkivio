package it.giovanni.arkivio.model.favorite

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.giovanni.arkivio.R

object FavoriteUtils {

    private fun getImageUrlByContentPath(contentPath: String?): String {
        // https://services.sg102.prd.sctv.ch/content/images/galaxy/deeplink/bluesport_navigation_H660.webp
        val domain = "https://services.sg102.prd.sctv.ch/content/images/"
        val imageSize = "H660"
        val imageFormat = "webp"
        return domain + contentPath + "_" + imageSize + "." + imageFormat
    }

    fun setImageByContentPath(imageView: ImageView, contentPath: String?) {
        val imageUrl: String = getImageUrlByContentPath(contentPath)
        val requestOptions: RequestOptions = RequestOptions()
            .placeholder(R.drawable.circle_item)
            .error(R.drawable.circle_item)
            // .transform(CircleCrop())
        if (imageUrl != null) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .apply(requestOptions)
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.circle_item)
        }
    }

    fun convertAvailableToFavorite(availables: MutableList<Available>): MutableList<Favorite> {
        val favorites: MutableList<Favorite> = mutableListOf()

        for (available in availables) {
            for (child in available.children) {
                val personal = Favorite(
                    domain = child.domain,
                    identifier = child.identifier,
                    kind = child.kind,
                    title = child.title,
                    contentPath = child.contentPath,
                    images = child.images,
                    availableTitle = available.title
                )
                favorites.add(personal)
            }
        }

        return favorites
    }

    fun getFavorites(): MutableList<Favorite> {
        return mutableListOf(
            Favorite(
                domain = "Galaxy",
                identifier = "mytv",
                kind = "World",
                title = "My TV",
                contentPath = "pages/regular/mytv",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/mytv_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "films",
                kind = "World",
                title = "Film",
                contentPath = "pages/regular/films",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/films_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "bluesport/home-de",
                kind = "CatalogStorePage",
                title = "",
                contentPath = "pages/catalogstore/bluesport/home-de",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/deeplink/bluesport_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "kids",
                kind = "World",
                title = "Bambini",
                contentPath = "pages/regular/kids",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/kids_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "replayguide",
                kind = "LandingPage",
                title = "Replay Guide",
                contentPath = "pages/regular/replayguide",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/replayguide_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "music-de",
                kind = "Dossier",
                title = "",
                contentPath = "pages/dossiers/music-de",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/dossiers/27c452aecc"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "comedy",
                kind = "World",
                title = "Comedy",
                contentPath = "pages/regular/comedy",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/comedy_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "krimi",
                kind = "World",
                title = "Crime & Thriller",
                contentPath = "pages/regular/krimi",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/crime_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "adventure",
                kind = "World",
                title = "Azione",
                contentPath = "pages/regular/adventure",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/action_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "series",
                kind = "World",
                title = "Serie",
                contentPath = "pages/regular/series",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/series_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "documentaries",
                kind = "World",
                title = "Docu",
                contentPath = "pages/regular/documentaries",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/documentaries_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "drama",
                kind = "World",
                title = "Drama",
                contentPath = "pages/regular/drama",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/drama_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "scifi",
                kind = "World",
                title = "Sci-Fi & Fantasy",
                contentPath = "pages/regular/scifi",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/scifi_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "horror",
                kind = "World",
                title = "Horror",
                contentPath = "pages/regular/horror",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/horror_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "western",
                kind = "World",
                title = "Western",
                contentPath = "pages/regular/western",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/western_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "nature",
                kind = "World",
                title = "Natura & Viaggi",
                contentPath = "pages/regular/nature",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/nature_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "history",
                kind = "World",
                title = "Storia",
                contentPath = "pages/regular/history",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/history_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "technology",
                kind = "World",
                title = "Scienza",
                contentPath = "pages/regular/technology",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/science_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "entertainment",
                kind = "World",
                title = "Shows",
                contentPath = "pages/regular/entertainment",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/shows_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "newsdiscussion",
                kind = "World",
                title = "Notizie & Talk",
                contentPath = "pages/regular/newsdiscussion",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/news_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "soap",
                kind = "World",
                title = "Soap & Reality",
                contentPath = "pages/regular/soap",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/soap_navigation"
                    )
                )
            ),
            Favorite(
                domain = "Galaxy",
                identifier = "lifestyle",
                kind = "World",
                title = "Lifestyle",
                contentPath = "pages/regular/lifestyle",
                images = listOf(
                    Image(
                        role = "Navigation",
                        contentPath = "galaxy/world/lifestyle_navigation"
                    )
                )
            )
        )
    }

    fun getAvailables(): MutableList<Available> {
        return mutableListOf(
            Available(
                domain = "Galaxy",
                identifier = "CategoryWorlds",
                kind = "Container",
                rootIdentifier = "1",
                title = "Categorie",
                children = listOf(
                    Child(
                        domain = "Galaxy",
                        identifier = "mytv",
                        kind = "World",
                        title = "My TV",
                        contentPath = "pages/regular/mytv",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/mytv_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "replayguide",
                        kind = "LandingPage",
                        title = "Replay Guide",
                        contentPath = "pages/regular/replayguide",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/replayguide_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "films",
                        kind = "World",
                        title = "Film",
                        contentPath = "pages/regular/films",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/films_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "series",
                        kind = "World",
                        title = "Serie",
                        contentPath = "pages/regular/series",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/series_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "kids",
                        kind = "World",
                        title = "Bambini",
                        contentPath = "pages/regular/kids",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/kids_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "documentaries",
                        kind = "World",
                        title = "Docu",
                        contentPath = "pages/regular/documentaries",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/documentaries_navigation"
                            )
                        )
                    )
                )
            ),
            Available(
                domain = "Galaxy",
                identifier = "GenresWorlds",
                kind = "Container",
                rootIdentifier = "2",
                title = "Genere",
                children = listOf(
                    Child(
                        domain = "Galaxy",
                        identifier = "comedy",
                        kind = "World",
                        title = "Comedy",
                        contentPath = "pages/regular/comedy",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/comedy_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "krimi",
                        kind = "World",
                        title = "Crime & Thriller",
                        contentPath = "pages/regular/krimi",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/crime_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "adventure",
                        kind = "World",
                        title = "Azione",
                        contentPath = "pages/regular/adventure",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/action_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "drama",
                        kind = "World",
                        title = "Drama",
                        contentPath = "pages/regular/drama",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/drama_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "soap",
                        kind = "World",
                        title = "Soap & Reality",
                        contentPath = "pages/regular/soap",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/soap_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "nature",
                        kind = "World",
                        title = "Natura & Viaggi",
                        contentPath = "pages/regular/nature",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/nature_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "scifi",
                        kind = "World",
                        title = "Sci-Fi & Fantasy",
                        contentPath = "pages/regular/scifi",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/scifi_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "western",
                        kind = "World",
                        title = "Western",
                        contentPath = "pages/regular/western",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/western_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "horror",
                        kind = "World",
                        title = "Horror",
                        contentPath = "pages/regular/horror",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/horror_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "history",
                        kind = "World",
                        title = "Storia",
                        contentPath = "pages/regular/history",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/history_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "technology",
                        kind = "World",
                        title = "Scienza",
                        contentPath = "pages/regular/technology",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/science_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "entertainment",
                        kind = "World",
                        title = "Shows",
                        contentPath = "pages/regular/entertainment",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/shows_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "newsdiscussion",
                        kind = "World",
                        title = "Notizie & Talk",
                        contentPath = "pages/regular/newsdiscussion",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/news_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "lifestyle",
                        kind = "World",
                        title = "Lifestyle",
                        contentPath = "pages/regular/lifestyle",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/world/lifestyle_navigation"
                            )
                        )
                    )
                )
            ),
            Available(
                domain = "Galaxy",
                identifier = "DeepLinks",
                kind = "Container",
                rootIdentifier = "3",
                title = "blue Entertainment",
                children = listOf(
                    Child(
                        domain = "Galaxy",
                        identifier = "bluesport/home-de",
                        kind = "CatalogStorePage",
                        title = "",
                        contentPath = "pages/catalogstore/bluesport/home-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/deeplink/bluesport_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "bluemax/home-de",
                        kind = "CatalogStorePage",
                        title = "",
                        contentPath = "pages/catalogstore/bluemax/home-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/deeplink/bluesupermax_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "bluepremium/home-de",
                        kind = "CatalogStorePage",
                        title = "",
                        contentPath = "pages/catalogstore/bluepremium/home-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/worlds/bluepremiumde_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "bluepremiumfr",
                        kind = "LandingPage",
                        title = "",
                        contentPath = "pages/regular/bluepremiumfr",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/worlds/bluepremiumfr_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "bluepremiumit",
                        kind = "LandingPage",
                        title = "",
                        contentPath = "pages/regular/bluepremiumit",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/worlds/bluepremiumit_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "bluevideo/home-de",
                        kind = "CatalogStorePage",
                        title = "",
                        contentPath = "pages/catalogstore/bluevideo/home-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/deeplink/bluevideo_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "tvguidecineseries",
                        kind = "DeepLink",
                        title = "",
                        contentPath = "/tvguide/grid/groups/C+CineSeries",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/deeplink/bluecineseries_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "blueplay/home-de",
                        kind = "CatalogStorePage",
                        title = "",
                        contentPath = "pages/catalogstore/blueplay/home-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/deeplink/blueplay_navigation"
                            )
                        )
                    ),
                    Child(
                        domain = "Galaxy",
                        identifier = "music-de",
                        kind = "Dossier",
                        title = "",
                        contentPath = "pages/dossiers/music-de",
                        images = listOf(
                            Image(
                                role = "Navigation",
                                contentPath = "galaxy/dossiers/7d5c6fb718"
                            )
                        )
                    )
                )
            )
        )
    }
}