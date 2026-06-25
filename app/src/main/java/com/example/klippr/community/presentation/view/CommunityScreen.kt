package com.example.klippr.community.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.klippr.community.domain.model.Review
import com.example.klippr.community.domain.model.ReviewComment
import com.example.klippr.community.presentation.viewmodel.CommunityViewModel
import com.example.klippr.favorites.domain.model.Favorite as FavoritePromotion
import com.example.klippr.favorites.presentation.viewmodel.FavoriteViewModel
import com.example.klippr.shared.presentation.component.KlipprBottomBar
import com.example.klippr.shared.presentation.component.KlipprTab
import com.example.klippr.shared.presentation.component.RemoteFavoriteHeartButton
import com.example.klippr.shared.presentation.component.rememberPromoDrawableId
import com.example.klippr.ui.theme.KlipprPurple
import java.text.SimpleDateFormat
import java.util.*

private val StarYellow      = Color(0xFFFFC107)
private val KlipprGreen     = Color(0xFF4CAF50)
private val KlipprLavender  = Color(0xFFF3EEFF)
private val LikePink        = Color(0xFFE91E63)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String = "current_user",
    promotionId: String? = null,
    onNavigateHome: () -> Unit = {},
    onNavigatePromos: () -> Unit = {},
    onNavigateMisPromos: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteState by favoriteViewModel.state.collectAsState()
    val favoriteByPromotion = remember(favoriteState.visibleFavorites) {
        favoriteByPromotionId(favoriteState.visibleFavorites)
    }

    LaunchedEffect(promotionId) {
        viewModel.setPromotionFilter(promotionId)
    }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotBlank()) {
            favoriteViewModel.loadFavorites(currentUserId)
        }
    }

    if (uiState.isReviewSheetOpen) {
        ReviewBottomSheet(
            uiState = uiState,
            onDismiss = { viewModel.closeReviewSheet() },
            onRatingChanged = viewModel::onRatingChanged,
            onCommentChanged = viewModel::onCommentChanged,
            onSubmit = viewModel::submitReview
        )
    }

    if (uiState.isCommentSheetOpen) {
        val reviewId = uiState.selectedReviewId.orEmpty()
        CommentBottomSheet(
            title = uiState.selectedReviewTitle ?: "Publicacion",
            comments = uiState.commentsByReviewId[reviewId].orEmpty(),
            draft = uiState.draftReplyComment,
            isLoading = uiState.isLoadingComments,
            isSubmitting = uiState.isSubmittingComment,
            onDraftChanged = viewModel::onReplyCommentChanged,
            onSubmit = viewModel::submitComment,
            onDismiss = viewModel::closeCommentSheet,
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (promotionId != null) "Reseñas" else "Comunidad",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = KlipprPurple
                )
            )
        },
        bottomBar = {
            KlipprBottomBar(
                current = KlipprTab.COMUNIDAD,
                onComunidad = { },
                onInicio = onNavigateHome,
                onFavoritos = onNavigateMisPromos,
                onPromos = onNavigatePromos,
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = KlipprPurple
                    )
                }
                uiState.reviews.isEmpty() -> {
                    EmptyFeedPlaceholder(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    ReviewFeed(
                        reviews = uiState.reviews,
                        commentsByReviewId = uiState.commentsByReviewId,
                        favoriteByPromotionId = favoriteByPromotion,
                        favoriteViewModel = favoriteViewModel,
                        currentUserId = currentUserId,
                        promotionId = promotionId,
                        onComment = viewModel::openCommentSheet,
                    )
                }
            }

            uiState.errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.dismissError() }) {
                            Text("OK", color = KlipprGreen)
                        }
                    }
                ) { Text(msg) }
            }
        }
    }
}

// ─── Feed de reseñas ─────────────────────────────────────────────────────────
@Composable
private fun ReviewFeed(
    reviews: List<Review>,
    commentsByReviewId: Map<String, List<ReviewComment>>,
    favoriteByPromotionId: Map<String, FavoritePromotion>,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    promotionId: String?,
    onComment: (Review) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (promotionId != null) "Reseñas de esta promoción" else "Lo que dice la comunidad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = KlipprPurple,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(reviews, key = { it.id }) { review ->
            ReviewCard(
                review = review,
                commentCount = commentsByReviewId[review.id]?.size,
                isFavorite = favoriteByPromotionId.containsKey(review.promotionId),
                favoriteViewModel = favoriteViewModel,
                currentUserId = currentUserId,
                onComment = { onComment(review) },
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ─── Card de reseña ──────────────────────────────────────────────────────────
@Composable
private fun ReviewCard(
    review: Review,
    commentCount: Int?,
    isFavorite: Boolean,
    favoriteViewModel: FavoriteViewModel,
    currentUserId: String,
    onComment: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            PromotionReviewImage(review)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.promotionTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = review.businessName,
                fontSize = 12.sp,
                color = KlipprPurple,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserAvatar(name = review.userName)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = review.userName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        if (review.isVerifiedPurchase) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = formatDate(review.createdAt),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                StarRatingDisplay(rating = review.rating)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )

            // US-16: reacción / like
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onComment) {
                    Icon(
                        imageVector = Icons.Outlined.RateReview,
                        contentDescription = "Comentar",
                        tint = KlipprPurple,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = commentCount?.takeIf { it > 0 }?.toString() ?: "Comentar",
                        fontSize = 12.sp,
                        color = KlipprPurple,
                    )
                }
                RemoteFavoriteHeartButton(
                    userId = currentUserId,
                    promotionId = review.promotionId,
                    isFavorite = isFavorite,
                    favoriteViewModel = favoriteViewModel,
                    selectedTint = LikePink,
                    unselectedTint = Color.Gray,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

// ─── Avatar circular con inicial ─────────────────────────────────────────────
@Composable
private fun PromotionReviewImage(review: Review) {
    val imageModel = review.promotionImageUrl.takeIf { it.isNotBlank() }
    val resId = rememberPromoDrawableId(imageModel)
    val imageModifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(KlipprLavender)

    Box(
        modifier = imageModifier,
        contentAlignment = Alignment.Center,
    ) {
        when {
            resId != 0 -> {
                Image(
                    painter = painterResource(resId),
                    contentDescription = review.promotionTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            isRemoteImageModel(imageModel) -> {
                AsyncImage(
                    model = imageModel,
                    contentDescription = review.promotionTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            else -> {
                Text(
                    text = review.promotionTitle.take(1),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = KlipprPurple.copy(alpha = 0.3f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentBottomSheet(
    title: String,
    comments: List<ReviewComment>,
    draft: String,
    isLoading: Boolean,
    isSubmitting: Boolean,
    onDraftChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "Comentarios",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KlipprPurple,
            )
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = KlipprPurple)
                    }
                }
                comments.isEmpty() -> {
                    Text(
                        text = "Aun no hay comentarios.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        textAlign = TextAlign.Center,
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(comments, key = { it.id.ifBlank { "${it.reviewId}-${it.createdAt}-${it.comment.hashCode()}" } }) { comment ->
                            CommentRow(comment)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = draft,
                onValueChange = onDraftChanged,
                label = { Text("Escribe un comentario") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubmit,
                enabled = draft.isNotBlank() && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = KlipprPurple),
                modifier = Modifier.fillMaxWidth().height(48.dp),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Publicar comentario", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CommentRow(comment: ReviewComment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        UserAvatar(name = comment.userName)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.userName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDate(comment.createdAt),
                    fontSize = 11.sp,
                    color = Color.Gray,
                )
            }
            Text(
                text = comment.comment,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun UserAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(KlipprPurple),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initial, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Badge "Compra verificada" ────────────────────────────────────────────────
@Composable
private fun VerifiedBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = "✓ Verificado", fontSize = 10.sp, color = KlipprGreen, fontWeight = FontWeight.Bold)
    }
}

// ─── Estrellas solo lectura ───────────────────────────────────────────────────
@Composable
private fun StarRatingDisplay(rating: Int) {
    Row {
        (1..5).forEach { i ->
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (i <= rating) StarYellow else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Placeholder feed vacío ───────────────────────────────────────────────────
@Composable
private fun EmptyFeedPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.RateReview,
            contentDescription = null,
            tint = KlipprPurple.copy(alpha = 0.4f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Aún no hay reseñas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KlipprPurple
        )
        Text(
            text = "¡Canjea una promoción y comparte tu experiencia! Ve a Mis Promos para dejar tu primera reseña.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "PE"))
    return sdf.format(Date(epochMillis))
}
